//final class SetupHeaderWriter {
//    void write(SetupHeader header, BitOutputStream bits, int audioChannels) throws IOException {
//        writeList(bits, 8, header.codebooks(), this::writeCodebook);
//        writeList(bits, 6, List.of(0), (value, out) -> out.write(16, value));
//        writeList(bits, 6, header.floors(), this::writeFloor);
//        writeList(bits, 6, header.residues(), this::writeResidue);
//        writeList(bits, 6, header.mappings(), (mapping, out) -> writeMapping(mapping, audioChannels, out));
//        writeList(bits, 6, header.modes(), this::writeMode);
//
//        // Framing flag
//        bits.write(1, 1);
//    }
//
//
//    private void writeCodebookOrdered(BitOutputStream bits, int[] codewordLengths) throws IOException {
//        int entries = codewordLengths.length;
//        for (int i = 0; i < entries; i++) {
//            if (codewordLengths[i] == 0 || (i > 0 && codewordLengths[i] < codewordLengths[i - 1])) {
//                throw new VorbisException("Ordered codebooks require non-decreasing, non-zero codeword lengths");
//            }
//        }
//
//        int length = entries == 0 ? 1 : codewordLengths[0];
//        bits.write(5, length - 1);
//
//        int entry = 0;
//        while (entry < entries) {
//            int number = 0;
//            while (entry + number < entries && codewordLengths[entry + number] == length) {
//                number++;
//            }
//            bits.write(Util.ilog(entries - entry), number);
//            entry += number;
//            length++;
//        }
//    }
//
//    private void writeFloor(Floor floor, BitOutputStream bits) throws IOException {
//        if (floor instanceof Floor1) {
//            bits.write(16, 1);
//            writeFloor1((Floor1) floor, bits);
//        } else {
//            throw new VorbisException("Invalid floor type");
//        }
//    }
//
//    private void writeFloor1(Floor1 floor1, BitOutputStream bits) throws IOException {
//        bits.write(5, floor1.partitionClassList().length);
//        for (int partitionClass : floor1.partitionClassList()) {
//            bits.write(4, partitionClass);
//        }
//
//        int numClasses = floor1.classDimensions().length;
//        for (int i = 0; i < numClasses; i++) {
//            bits.write(3, floor1.classDimensions()[i] - 1);
//            int classSubclass = floor1.classSubclasses(i);
//            bits.write(2, classSubclass);
//            if (classSubclass != 0) {
//                bits.write(8, floor1.classMasterBooks()[i]);
//            }
//            for (int j = 0; j < 1 << classSubclass; j++) {
//                bits.write(8, floor1.subclassBooks()[i][j] + 1);
//            }
//        }
//
//        int rangeBits = Integer.numberOfTrailingZeros(floor1.xList()[1]);
//        bits.write(2, floor1.multiplier() - 1);
//        bits.write(4, rangeBits);
//
//        for (int i = 2; i < floor1.xList().length; i++) {
//            bits.write(rangeBits, floor1.xList()[i]);
//        }
//    }
//
//    private void writeResidue(Residue residue, BitOutputStream bits) throws IOException {
//        bits.write(16, residue.type());
//        bits.write(24, residue.begin());
//        bits.write(24, residue.end());
//        bits.write(24, residue.partitionSize() - 1);
//        bits.write(6, residue.classifications() - 1);
//        bits.write(8, residue.classBook());
//
//        for (int[] classBooks : residue.books()) {
//            int cascade = 0;
//            for (int j = 0; j < 8; j++) {
//                if (classBooks[j] != -1) {
//                    cascade |= 1 << j;
//                }
//            }
//
//            bits.write(3, cascade);
//            boolean hasHigh = cascade > 7;
//            bits.writeBool(hasHigh);
//            if (hasHigh) {
//                bits.write(5, cascade >> 3);
//            }
//        }
//
//        for (int[] classBooks : residue.books()) {
//            for (int book : classBooks) {
//                if (book != -1) {
//                    bits.write(8, book);
//                }
//            }
//        }
//    }
//
//    private void writeMapping(Mapping mapping, int audioChannels, BitOutputStream bits) throws IOException {
//        int submaps = mapping.submaps();
//        int couplingSteps = mapping.couplingSteps();
//
//        bits.write(16, 0);
//
//        bits.writeBool(submaps > 1);
//        if (submaps > 1) {
//            bits.write(4, submaps - 1);
//        }
//
//        bits.writeBool(couplingSteps > 0);
//        if (couplingSteps > 0) {
//            int audioChannelLog = Util.ilog(audioChannels - 1);
//            bits.write(8, couplingSteps - 1);
//            for (int i = 0; i < couplingSteps; i++) {
//                bits.write(audioChannelLog, mapping.magnitude()[i]);
//                bits.write(audioChannelLog, mapping.angle()[i]);
//            }
//        }
//
//        bits.write(2, 0); // reserved
//
//        if (submaps > 1) {
//            for (int i = 0; i < audioChannels; i++) {
//                bits.write(4, mapping.mux()[i]);
//            }
//        }
//
//        for (int i = 0; i < submaps; i++) {
//            bits.write(8, 0);
//            bits.write(8, mapping.submapFloor()[i]);
//            bits.write(8, mapping.submapResidue()[i]);
//        }
//    }
//
//    private void writeMode(Mode mode, BitOutputStream bits) throws IOException {
//        bits.writeBool(mode.blockFlag());
//        bits.write(16, 0);
//        bits.write(16, 0);
//        bits.write(8, mode.mapping());
//    }
//
//    private <T> void writeList(BitOutputStream bits, int countBits, List<T> list, Writer<T> writer) throws IOException {
//        if (list.isEmpty() || list.size() > 1 << countBits) {
//            throw new IllegalArgumentException("Too many elements");
//        }
//        bits.write(countBits, list.size() - 1);
//        for (T value : list) {
//            writer.write(value, bits);
//        }
//    }
//
//    private interface Writer<T> {
//        void write(T value, BitOutputStream bits) throws IOException;
//    }
//}
