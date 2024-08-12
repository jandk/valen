package be.twofold.valen;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.reader.resource.*;
import be.twofold.valen.reader.streamdb.*;
import be.twofold.valen.resource.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

public final class DbImporter {
    private static final String TABLE_SQL = """
        create table if not exists file
        (
            id   int primary key,
            name varchar(255) not null
        );
        create table if not exists map
        (
            id   int primary key,
            name varchar(255) not null
        );
        create table if not exists map_file
        (
            map_id  int not null references map (id),
            file_id int not null references file (id),
            primary key (map_id, file_id),
            unique (file_id, map_id)
        );
        create table if not exists stream
        (
            id      serial primary key,
            file_id int    not null references file (id),
            hash    bigint not null,
            start   bigint not null,
            size    int    not null
        );
        create table if not exists resource
        (
            id               serial primary key,
            file_id          int          not null references file (id),
            name             varchar(511) not null,
            type             varchar(255) not null,
            variation        varchar(255) not null,
            start            int          not null,
            size             int          not null,
            uncompressedSize int          not null,
            dataCheckSum     bigint       not null,
            defaultHash      bigint       not null,
            timestamp        timestamp    not null,
            version          int          not null,
            flags            int          not null,
            compMode         varchar(255) not null
        );
        """;

    private static final Path EXECUTABLE = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOMEternal\\DOOMEternalx64vk.exe");
    private static final Path BASE = EXECUTABLE.getParent().resolve("base");
    private final Connection connection;

    public DbImporter(Connection connection) {
        this.connection = connection;
    }

    public static void main(String[] args) throws Exception {
        var spec = PackageMapSpecReader.read(BASE.resolve("packagemapspec.json"));

        try (var connection = DriverManager.getConnection("jdbc:postgresql://192.168.1.201:5432/doom?rewriteBatchedQueries=true", "doom", "doom")) {
            try (var statement = connection.createStatement()) {
                statement.execute(TABLE_SQL);
            }

            var files = spec.files();
            var dbImport = new DbImporter(connection);
            dbImport.insertNames("file", files);
            dbImport.insertNames("map", spec.maps());
            dbImport.insertMapFiles(spec.mapFiles());
            dbImport.insertStreamDbs(files);
            dbImport.insertResources(files);
        }
    }

    // region PackageMapSpec.json

    private void insertNames(String table, List<String> names) throws SQLException {
        try (var statement = connection.prepareStatement("insert into " + table + "(id, name) values (?, ?)")) {
            for (var i = 0; i < names.size(); i++) {
                statement.setInt(1, i);
                statement.setString(2, names.get(i));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertMapFiles(Map<String, List<String>> mapFiles) throws SQLException {
        try (var statement = connection.prepareStatement("insert into map_file(map_id, file_id) select m.id, f.id from map m, file f where m.name = ? and f.name = ?")) {
            for (var e : mapFiles.entrySet()) {
                var map = e.getKey();
                for (var file : e.getValue()) {
                    statement.setString(1, map);
                    statement.setString(2, file);
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
    }

    // endregion

    // region .resources

    private void insertResources(List<String> files) throws SQLException {
        var resourceFiles = IntStream.range(0, files.size())
            .mapToObj(i -> Map.entry(i, files.get(i)))
            .filter(e -> e.getValue().endsWith(".resources"))
            .toList();
        for (var e : resourceFiles) {
            insertResources(e.getKey(), e.getValue());
        }
    }

    private void insertResources(int fileId, String path) throws SQLException {
        var sql = "insert into resource(file_id, name, type, variation, start, size, uncompressedSize, dataCheckSum, defaultHash, timestamp, version, flags, compMode) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (var channel = Files.newByteChannel(BASE.resolve(path));
             var statement = connection.prepareStatement(sql)
        ) {
            var reader = new ChannelDataSource(channel);
            var resources = mapResources(Resources.read(reader));
            System.out.println("Importing " + resources.size() + " resource entries from " + path);

            statement.setInt(1, fileId);
            insert(statement, resources, (preparedStatement, entry) -> {
                statement.setString(2, entry.name());
                statement.setString(3, entry.type());
                statement.setString(4, entry.variation());
                statement.setInt(5, entry.offset());
                statement.setInt(6, entry.size());
                statement.setInt(7, entry.uncompressedSize());
                statement.setLong(8, entry.dataCheckSum());
                statement.setLong(9, entry.defaultHash());
                statement.setTimestamp(10, new Timestamp(entry.timestamp().getTime()));
                statement.setInt(11, entry.version());
                statement.setInt(12, entry.flags());
                statement.setString(13, entry.compMode());
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<ResourceEntity> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResource(resources, entry))
            .toList();
    }

    private ResourceEntity mapResource(Resources resources, ResourcesEntry entry) {
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + 1]);
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings()]);
        var variation = ResourceVariation.fromValue(entry.variation()).name();
        var offset = entry.dataOffset();
        var size = entry.dataSize();
        var uncompressedSize = entry.uncompressedSize();
        var dataCheckSum = entry.dataCheckSum();
        var defaultHash = entry.defaultHash();
        var timestamp = new Timestamp(entry.generationTimeStamp() / 1000);
        var version = entry.version();
        var flags = entry.flags();
        var compMode = entry.compMode().toString();

        return new ResourceEntity(
            name,
            type,
            variation,
            offset,
            size,
            uncompressedSize,
            dataCheckSum,
            defaultHash,
            timestamp,
            version,
            flags,
            compMode
        );
    }

    // endregion

    // region .streamdb

    private static StreamDb readStreamDb(String relativePath) {
        try (var channel = Files.newByteChannel(BASE.resolve(relativePath))) {
            return StreamDb.read(new ChannelDataSource(channel));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void insertStreamDbs(List<String> files) throws SQLException {
        var streamFiles = IntStream.range(0, files.size())
            .mapToObj(i -> Map.entry(i, files.get(i)))
            .filter(e -> e.getValue().endsWith(".streamdb"))
            .toList();
        for (var e : streamFiles) {
            insertStreamDb(e.getKey(), e.getValue());
        }
    }

    private void insertStreamDb(int fileId, String path) throws SQLException {
        try (var statement = connection.prepareStatement("insert into stream(file_id, hash, start, size) values (?, ?, ?, ?)")) {
            var entries = readStreamDb(path).entries();
            System.out.println("Importing " + entries.size() + " stream entries from " + path);

            statement.setInt(1, fileId);
            insert(statement, entries, (preparedStatement, entry) -> {
                statement.setLong(2, entry.identity());
                statement.setLong(3, entry.offset16() * 16L);
                statement.setInt(4, entry.length());
            });
        }
    }

    // endregion

    // region Helpers

    private static <T> void insert(
        PreparedStatement statement,
        List<T> entries,
        PreparedStatementSetter<T> consumer
    ) throws SQLException {
        var monitor = new ProgressMonitor(entries.size());
        for (var entry : entries) {
            consumer.setValues(statement, entry);
            statement.addBatch();
            if (monitor.increment()) {
                statement.executeBatch();
            }
        }
        statement.executeBatch();
    }

    private interface PreparedStatementSetter<T> {
        void setValues(PreparedStatement statement, T entry) throws SQLException;
    }

    private static final class ProgressMonitor {
        private final int total;
        private long lastTimestamp = System.nanoTime();
        private int lastCount;
        private int count;

        private ProgressMonitor(int total) {
            this.total = total;
        }

        private boolean increment() {
            count++;
            if (System.nanoTime() > lastTimestamp + 1_000_000_000L) {
                lastTimestamp = System.nanoTime();
                print("Progress: " + count + "/" + total + " (" + (count - lastCount) + " per second)");
                lastCount = count;
                return true;
            }
            return false;
        }

        private void print(String s) {
            System.out.print("\r");
            System.out.print(s);
            System.out.flush();
        }
    }

    record ResourceEntity(
        String name,
        String type,
        String variation,
        int offset,
        int size,
        int uncompressedSize,
        long dataCheckSum,
        long defaultHash,
        Timestamp timestamp,
        int version,
        int flags,
        String compMode
    ) {
    }

    // endregion

}
