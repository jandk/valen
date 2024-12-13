package be.twofold.valen;

import be.twofold.valen.ui.*;
import javafx.application.*;

import java.io.*;

public final class Main {
    public static void main(String[] args) throws IOException {
        String hex = """
            F1 13 FB F8 ED E1 D5 CA BF B5 AC A5 A2 9F 9D 9F A5 AE F4 F4 EC E0 D4 C8 BD B2 A9 A1 9D 9A 98 9B
                                               A1 A9 EE EE 10 00 F0 34 B3 A9 A2 9E 98 96 97 9C A5 E9 E9 EA E2 D6 CB C0 B6 AD A7 9F 97 94 97 9C
                                               A5 E5 E5 E6 E5 DA CF C5 BC B4 AC A3 9B 98 98 9C A3 E3 E3 E4 E7 DF D5 CC C4 43 B2 56 60 98 96 9A
                                               A0 E3 E2 E4 E7 E6 DD D4 CB BF B4 10 00 F1 13 9B A2 E4 E3 E5 E7 EB E2 D6 C9 BE B2 A7 9C 92 8E 92
                                               9C E4 E4 E5 E7 EC E1 D5 C9 42 4E 59 9A 90 8A 90 9A 10 00 B2 E2 D6 CA BE 4C 57 9D 93 8F 94 9E 30
                                               00 F1 8F D8 CC 3F 4B A9 9F 98 95 99 A0 E5 E4 E5 E8 E3 DA D1 C8 C0 B4 A9 60 98 95 9A A3 E7 E6 E8
                                               E7 DD D3 C9 C1 48 AF 58 60 9B 98 9B A2 EB EA EB E2 D7 CD C3 B9 4F A7 A0 99 94 95 9A A1 F0 EF E9
                                               DE D3 C8 BE B4 AB A2 99 96 93 95 99 A1 F6 F3 E7 DB D0 C5 BB B1 A8 A0 98 94 96 98 9D A5 FD F3 E7
                                               DB CF C4 BA B1 A8 A1 9C 99 98 9C A3 AA FF F4 E8 DC D1 C6 BC B3 AC A6 A3 9F 9E A2 A9 B2 F1 F0 E9
                                               DD D1 C5 BA AF A4 9C 98 96 93 96 9E A9 E9 E9 E8 DC D0 C4 B8 AC A1 97 91 8F 8C 90 9A A4 E3 E2 E4
                                               DD 10 00 F3 58 67 92 8E 8A 8B 94 A0 DD DD DE DF D3 C7 BB 4F A6 9E 68 8E 87 8C 94 9F D9 D9 DA DD
                                               D7 CB C1 B7 AE 58 62 6B 73 8C 92 9A D7 D6 D8 DB DC D2 C8 40 B8 51 5B 67 71 8B 8F 97 D6 D6 D7 DB
                                               DF D9 D1 C8 43 4F 5C 67 72 8A 92 9A D7 D7 D8 DB E0 DF D3 38 45 51 5D 69 74 89 8F 99 D8 D7 D8 DB
                                               1F 20 D3 39 45 52 5E 6A 75 7E 8C 96 20 00 92 C7 44 50 5C 67 73 79 8D 97 10 00 70 2B C8 43 4E 5B
                                               66 71 30 00 F1 13 D8 D9 DC E0 D7 CE C6 42 4F 5B 67 71 8A 92 9C DB DA DC DE D9 CF C5 BC 4B 53 5C
                                               66 91 8C 90 99 DF DE E0 10 01 F0 BA 54 5C 65 6D 89 8C 91 9B E4 E4 E5 DA CE C3 B8 51 5B 9C 92 74
                                               78 89 8F 9A EB EB E4 D7 CB C0 B4 AA A0 96 8F 77 89 8D 95 9F F3 EF E3 D7 CB BF B3 A8 9F 97 92 8F
                                               8D 93 9C A6 FA F0 E4 D8 CC C1 B5 AB A2 9C 9A 94 94 9A A3 AD E7 E5 E6 D9 CD C1 B5 AA A0 97 91 90
                                               8B 90 9A A6 DF DF DF D9 CC C0 B4 A8 9C 90 86 88 7E 8A 97 A3 D8 D8 D9 D9 CD C1 B6 56 9D 6D 76 7D
                                               7D 87 92 9D D2 D1 D3 D6 D0 C5 B9 51 5C 65 6D 75 7F 7A 8D 96 CD CD CE D2 D5 CA BF B4 54 5B 63 6D
                                               87 7C 8D 93 CA CA CC CF D5 2F C6 42 4A 52 5C 69 75 80 85 92 CA C9 CB CF D4 D6 31 38 BB 50 5D 69
                                               76 81 8D 95 CB CA CC CF 2A D5 CD C5 46 52 5E 6A 77 83 8C 97 CB 30 00 B1 D7 CF 3C 48 54 60 6C 78
                                               81 88 94 20 00 93 28 CE 3B 47 54 60 6B 77 7F 10 00 F0 04 D6 CD 3A 46 53 5F 6A 76 82 8B 96 CC CB
                                               CD D0 D5 D5 33 3B 60 01 F0 80 75 81 8E 95 CF CE D0 D3 D6 CC C3 45 4D 54 5E 69 74 81 89 93 D3 D3
                                               D4 D8 D0 C5 BB 4D 57 5E 66 6F 7A 7D 8D 95 DA D9 DB D7 CB C0 4B 55 5F 68 70 78 7F 82 8C 99 E1 E1
                                               E1 D5 C9 BC B0 A4 99 70 79 7D 7F 89 93 9D EA E7 E1 D4 C8 BB AF A3 97 72 88 78 85 90 9B A4 F0 ED
                                               E2 D6 C9 BD B1 A6 9B 94 92 8A 8C 96 A1 AC DD DB DC D8 CB BF B3 A7 9B 91 8C 8D 8C 91 9B A7 D5 D4
                                               D4 D6 CA BE B1 A5 98 8C 84 7C 7C 8B 97 A3 CE CD CE D0 CC BF B3 A7 64 6D 76 50 00 F0 2B 99 C7 C6
                                               C8 CC CF C3 48 AD 5C 64 6C 76 7E 82 8D 8E C1 C1 C3 C7 CD 36 41 B5 53 5B 66 70 7B 7D 7A 89 BE BD
                                               C0 C4 CA CE C6 42 4C 56 61 6C 78 80 7C 8A BD BD BF C3 36 CD 3B BC 4A F0 00 F0 19 83 8B 90 BE BE
                                               BF C4 35 32 3B 43 4A 55 61 6D 79 85 8F 9A BF BE C0 C4 35 31 3A 42 4C 58 63 70 7B 7F 8B 97 BE BE
                                               C0 C4 CA 10 00 32 57 62 6F 10 00 33 BF C4 CA 30 00 D0 7A 85 8E 98 BF BF C1 C5 CB CD 3A 42 4B 40
                                               00 F0 27 84 87 8E C3 C2 C4 C8 CE 33 C2 45 4D 57 62 6C 78 7E 7A 89 C8 C8 CA CD CF C4 BA 4E 56 5D
                                               67 72 7C 7F 77 8D D0 CF D1 D3 CB BF 4C 57 60 68 6F 78 7F 84 8F 98 D8 D7 D6 10 01 F3 0C 5C 97 72
                                               79 80 80 8B 95 A0 DF DC DB D4 C7 BA AE A1 95 76 83 7C 85 90 9C A6 E5 E3 40 01 B0 91 8F 89 8B 96
                                               A1 AC D4 D2 D3 D5 00 01 F1 79 9C 92 8C 89 88 93 9E A9 CB CA CA CD CB BE B2 A5 99 72 7F 7A 85 8F
                                               9A A0 C4 C2 C3 C6 CB BF B3 58 9C 6D 77 7F 80 8C 90 95 BC BC BD C0 C6 C2 47 52 5C 66 70 7A 7C 86
                                               84 8D B6 B5 B8 BD C3 C4 44 4D 57 61 6A 75 7F 81 7D 89 B2 B1 B4 B9 3F 3A 42 49 53 5D 67 72 7D 7D
                                               7F 8A B1 B0 4C B8 40 C4 44 4C 54 5B 66 70 7C 87 8A 90 B2 B1 B3 B8 3F 3B 44 4D 55 5B 66 71 7D 88
                                               93 9B B2 B1 B3 47 3F 38 42 4B 53 5D 68 73 7F 83 8F 9B 20 00 82 C6 42 4C 54 5E 67 73 7E 10 00 16
                                               47 30 00 F0 47 92 98 B3 B2 4A BA 3E 3B 44 4C 53 5C 66 71 7D 85 87 8E B8 B7 B9 BE C4 C5 BC 4C 55
                                               5D 68 72 7D 7D 7E 89 BE BD 40 3B C8 3C B9 4F 59 62 6C 76 7E 83 7A 8B C6 C6 C6 C7 35 C0 B5 54 5E
                                               68 71 7C 7D 88 8D 93 CF CC CA CB C9 BD B1 59 64 6F 79 7E 81 8D 95 9C D4 D1 D0 D1 10 01 30 66 72
                                               7C 1F 03 F1 07 A4 DB D9 D8 D6 CA BE B2 A7 9D 95 90 8A 8E 97 A2 AD CC CA CB CD CA 20 01 F4 8D 8B
                                               8D 90 96 9F A8 C2 C0 C1 C4 C7 BC B1 A5 98 73 81 7E 86 8F 97 9F BA B8 B9 BC C1 BB B0 5B 65 70 7B
                                               7B 81 78 8D 96 B3 B1 B2 B6 BC 45 4E 58 62 6B 75 7F 81 7B 85 90 AB AA AD 4D B9 43 4C 55 5E 68 70
                                               7A 7C 80 81 8D A6 A5 A8 50 47 43 4C 53 5B 65 6E 78 80 7C 83 8D A4 A3 A7 51 48 44 4D 56 5E 64 6D
                                               77 82 87 8B 93 A6 A4 A7 51 48 42 4C 56 5F 66 6D 77 82 8D 97 9C A6 A5 A8 AE 49 3F 49 53 5D 64 6E
                                               78 7F 88 94 9F A6 5A 57 51 49 40 4A 54 5D 65 6F 78 7F 89 94 9F A5 A4 58 AE 48 43 30 00 F0 BF 93
                                               99 A7 A6 A9 AF 47 44 4D 55 5D 63 6D 77 82 84 88 91 AD AC AE B4 43 43 4B 53 5C 65 6F 79 7E 7F 81
                                               8D B5 B4 B6 46 42 44 4E 57 60 6A 73 7B 7D 7F 82 8D BE BC BA BB BF 45 50 5A 64 6D 77 7F 81 7C 88
                                               92 C4 C0 BF C0 C3 45 AE 5B 67 72 7C 7B 7F 74 8F 9A CB C7 C6 C7 C7 BB B0 5B 97 73 82 81 89 90 99
                                               A1 D2 CF CE CF CA BF 4C A7 9C 92 8D 8C 92 9A A1 AA C5 C3 C3 C4 C6 BB B0 5A 9B 93 90 90 8E 94 9E
                                               A7 BB B8 B9 BB BF B7 AC 5E 6A 75 7A 84 82 8C 96 A0 B1 AF AF B3 B8 B4 56 5F 6B 77 80 80 7C 86 90
                                               9A AA A6 A7 AC B3 4B 56 60 6A 73 7C 7C 80 81 8A 95 A1 A1 A2 A7 50 4A 54 5D 66 6F 76 7F 82 7B 86
                                               92 9A 99 9E 59 AF 4B 55 5D 64 6D 76 80 92 01 50 98 97 63 5A 50 FF 03 F2 18 6E 75 7F 86 88 8D 97
                                               9A 67 63 5A 50 48 53 5D 67 71 75 7E 89 92 98 9F 9A 98 63 5A 50 45 4F 5A 64 6D 75 7F 83 8E 99 A5
                                               10 00 B0 50 5A 65 6F 76 80 84 8F 9A A5 99 10 00 10 49 FF 01 F0 0D 75 7F 89 90 95 9C 9C 9B 9F 58
                                               4F 4B 55 5F 67 6C 75 7F 82 84 8B 95 A3 A2 A5 54 4D 4B 8E 05 F0 1F 77 7F 7F 7B 86 92 AD AC AB 52
                                               B1 4A 55 5F 69 71 79 7F 83 7D 87 92 B5 B0 AE B0 B4 4C 57 61 6B 75 7E 7D 7F 83 8C 96 BA B6 B4 B5
                                               B9 B5 A9 62 2F 01 F0 06 7D 87 92 9B C2 BE BC BD C1 B8 AC 5E 96 73 79 86 85 8F 98 A2 CA 30 01 F0
                                               46 BC B1 A6 9C 92 8E 90 91 97 A0 A9 C0 BD BC BD C0 B8 AE A4 9A 93 92 92 92 98 A1 AA B5 B2 B1 B3
                                               B7 B3 A7 9D 94 89 76 8A 89 91 9A A5 AA A7 A8 AA AF AF A4 66 8E 7A 7B 80 82 8B 95 9F A1 9D 9E 5C
                                               A9 52 5D 67 72 7B 7E 7E 7C 85 90 9A 9A 95 97 61 58 51 5B 65 6E 75 D6 09 F0 35 8C 97 91 8F 95 62
                                               58 53 5D 66 6E 76 7E 7C 7D 82 8D 97 8D 8B 6C 61 57 51 5C 67 71 78 7D 86 86 8A 92 9C 8F 74 6C 61
                                               56 4E 58 63 6D 78 7D 86 8F 95 9B A2 8F 8C 6C 61 56 4B 53 5F 6B 76 7E 7F 88 93 9F AA 8F 73 10 00
                                               F0 21 54 5F 6A 76 7F 80 89 94 A0 A9 8E 8B 6C 61 56 4F 5A 64 6F 79 7D 87 8F 92 98 A0 93 6F 69 60
                                               57 52 5D 67 71 76 7E 83 82 87 90 9A 9C 9A 9C 5E 58 52 1F 01 20 7E 7C 80 00 F0 21 A5 A2 9F 5E 58
                                               51 5B 66 70 78 7E 7E 7E 82 8D 98 AC A5 A3 A5 55 52 5C 68 73 7D 7D 80 7E 87 91 9B B2 AD AA AC 4E
                                               4E 5A 66 70 7C 7A 83 84 8C 96 A1 30 01 C0 B8 B4 A9 9F 95 8B 79 8A 8B 93 9C A6 60 02 F0 05 C0 BA
                                               B0 A6 9C 96 92 94 95 9A A3 AC BC B8 B6 B6 B7 4A AD A3 A9 0B F0 77 9A 9F A7 AF B1 AD AB AC AE AD
                                               59 9B 92 8F 8D 8F 93 98 A0 AA A6 A2 A0 5D A7 A8 60 6A 75 7B 81 86 8C 92 9A A4 9B 97 96 99 5E 59
                                               63 6E 79 80 7B 80 85 8C 96 A0 93 8B 71 6A 61 59 62 6B 74 7C 7A 81 7F 88 92 9D 8D 88 74 6A 60 5A
                                               64 6E 77 7E 7E 7E 7F 89 93 9E 88 7D 72 68 5D 58 62 6C 77 7D 80 85 88 8E 97 A1 89 7D 73 67 5A 54
                                               5C 67 71 82 82 89 91 98 9F A7 89 7E 72 66 5A 53 56 61 6E 85 81 84 8D 98 A4 AF 89 7D 10 00 A0 58
                                               9C 6E 79 80 82 8E 99 A4 AD 30 00 F0 01 5B 54 5E 68 73 81 84 8B 90 95 9C A5 8D 89 72 68 80 00 40
                                               78 7D 81 81 80 00 40 95 90 91 69 70 00 B0 75 7F 7C 82 7E 88 92 9D 9E 97 92 90 00 F0 01 6C 75 7D
                                               79 7F 81 89 93 9D A4 9C 99 64 5D 58 63 3F 01 F1 B6 82 87 8D 97 A1 AB A5 A3 A4 A7 AB 5E 97 73 79
                                               85 89 8E 93 9C A6 B5 B0 AE AE AE 4E A8 9D 95 6D 8E 90 95 9A A2 AB BF BB BA B7 B7 B9 AF A6 A0 9B
                                               96 98 9D A1 A8 B1 BA B5 B1 AF B1 B1 AB A6 A1 9F 9D 9F A4 A7 AE B6 AF A9 A7 A4 A6 A8 A0 9B 97 93
                                               92 95 9C A0 A7 B0 A5 9E 9B 9B 62 A2 67 6E 72 77 86 8D 94 9A A1 AA 9A 94 8E 6D 67 60 6B 74 7E 79
                                               80 86 8F 94 9C A6 91 8A 85 73 69 62 6A 71 7C 7C 7E 83 87 90 99 A4 8D 83 7B 71 67 63 6C 76 7E 7E
                                               7E 7D 84 90 9A A4 89 7D 76 6C 64 61 67 71 84 82 83 88 8C 94 9D A7 88 7E 74 6A 64 5F 61 6A 8B 83
                                               80 8A 95 9C A3 AC 89 7F 74 6A 64 5E 59 62 94 8A 86 8A 93 9E A9 B4 20 00 B2 5E 5C 9B 91 89 84 87
                                               90 9C A8 B1 30 00 F0 A7 63 6C 89 80 7F 8A 94 99 A1 AB 89 80 77 6E 63 61 69 72 82 80 82 85 89 92
                                               9C A6 90 89 79 72 68 63 6C 75 7C 7D 80 7D 84 8F 99 A4 99 8F 87 72 6B 62 68 72 7D 7B 7F 83 89 90
                                               9A A4 A0 96 92 6B 68 62 67 72 7A 79 83 87 90 95 9E A7 A8 A1 9E 9C 9C 5E 62 6B 6E 8C 8B 8F 96 9B
                                               A3 AC B2 AD A9 A5 A5 A9 A6 9F 65 98 95 98 9E A2 A9 B1 BD B9 B3 B0 B0 B3 B0 A9 A4 A2 9F A0 A6 A9
                                               AF B8 BA B2 AE AB AA AA A9 A6 A4 A1 A1 A5 AB B1 B6 BD AF A8 A2 9F 9F A0 9F 9A 99 95 96 9C A2 AA
                                               AF B7 A5 9E 97 93 6A 66 6A 72 71 8A 8E 92 9A A3 A9 B2 9E 94 8D 76 70 69 6E 7A 79 80 89 8A 40 09
                                               F0 FF 4A 95 8D 84 7B 6F 6C 71 79 7D 7D 84 7A 8A 93 9F AB 8F 86 7E 75 6D 6F 75 82 7D 7E 7E 82 8A
                                               94 9F AB 8D 83 7A 73 70 6C 6E 89 80 80 85 8D 92 9A A3 AE 90 85 7B 73 70 69 99 91 8B 88 85 8E 98
                                               A1 A9 B2 90 86 7B 71 6F 66 5F 9C 97 92 8E 92 9B A4 AE B9 90 86 7B 73 6F 66 9F 98 93 8F 8B 90 99
                                               A2 AC B7 8F 85 7B 72 70 69 69 8E 88 85 84 8E 99 9F A7 B1 8E 84 7D 73 6F 6D 70 87 7F 80 86 8A 8F
                                               98 A2 AD 91 88 7E 77 6D 6F 75 7E 7C 80 7D 80 88 93 9E AB 98 90 87 7C 74 6A 6F 75 7C 7E 87 87 8D
                                               95 A0 AB A0 97 90 8B 73 6B 6C 71 77 83 8A 8C 95 9D A5 AE A8 A1 9A 94 95 66 68 6A 92 90 91 95 9C
                                               A5 AB B3 B2 AB A4 A0 A0 A1 A0 9F 9E 9B 9B 9F A5 AC B1 B9 BC B5 AF AC AB AC AB AA AA A6 A6 A8 AE
                                               B3 B8 BF B9 B2 AC A8 A7 A5 A4 A7 A6 A6 A7 AB B1 B7 BF C5 B0 A8 A1 9C 9A 9A 98 9A 99 9C 9E A3 A8
                                               AF B8 BF A8 9E 96 90 8E 70 72 8F 8D 91 96 9A A0 A8 AF B8 A1 97 8D 85 7A 76 76 7A 82 8A 90 94 9A
                                               A0 A8 B2 9B 92 88 80 78 73 76 78 7E 84 86 8C 93 9B A4 AF 97 8E 86 7D 77 7C 81 7F 7F 7D 82 89 91
                                               9A A4 AF 95 8B 82 7B 7D 75 88 83 7F 82 8C 92 98 9F A8 B2 96 8B 80 7C 7A 70 91 8E BF 01 F0 0F 9F
                                               A7 AF B8 97 8D 82 7B 78 6E 95 9A 98 97 97 9A A2 AB B5 BF 97 8D 83 7B 78 6F 94 96 95 94 2E 02 F0
                                               1F B4 BD 95 8B 81 7D 7A 70 8E 8A 88 87 8D 95 9E A5 AD B6 95 8C 84 7B 7C 77 87 81 7F 84 8A 8F 95
                                               9D A7 B1 98 90 87 7E 75 7A 7F 7E 81 7D 7E 88 70 00 F0 42 9D 93 89 81 79 73 74 79 7F 86 88 8F 94
                                               9C A5 B0 A3 99 8F 86 7A 75 78 7C 85 8B 92 95 9C A2 AA B4 AA A1 98 92 8F 6E 70 8E 91 94 98 9D A2
                                               AA B2 BA B3 AA A2 9D 9B 9C 9B 9B 9D 9F A2 A6 AA B1 BA C0 BC B4 AE A9 A7 A8 A7 A7 A9 AB AC AF B4
                                               BA C1 C7 BB 10 00 F0 70 A4 A4 A7 A8 AB AF B3 B8 BD C5 CD B3 AA A3 9D 9B 98 97 9C 9D A1 A6 AC AF
                                               B6 BE C5 AC A2 99 93 90 8C 8B 91 93 98 9F A4 A9 B0 B6 BE A6 9D 94 8C 86 82 83 87 8C 93 96 9B A1
                                               A8 B0 B8 A3 9A 91 89 81 7D 7E 82 87 88 8F 94 9B A3 AC B6 9F 97 8F 86 7D 7D 7D 81 7D 7D 8A 92 9A
                                               A3 AC B6 9D 94 8B 82 7E 82 80 7F 82 8B 90 97 9E A6 AF B9 9C 92 8A 80 7F 84 89 8C 8C 90 97 9F A6
                                               AD B5 BE 9E 94 89 7F 7A 04 F0 15 98 9A 9F A4 AB B3 BC C5 9E 93 8A 7F 81 84 8C 95 94 97 9C A2 AA
                                               B2 BB C3 9C 92 8A 81 7E 85 87 88 88 8D 95 9E 21 00 B0 9D 94 8C 83 7D 80 7E 7F 82 88 8E 0F 02 B1
                                               B8 A0 98 8F 86 7E 7C 7E 82 80 7B 70 00 F0 18 B5 A4 9A 92 8A 83 7E 80 84 89 8B 90 96 9D A4 AD B6
                                               A8 9E 95 8D 87 84 85 88 8E 94 99 9E A4 AA B1 BA AE A4 9B 94 8F 8F 6C 04 F1 07 A1 A6 AA B1 B8 C0
                                               B5 AC A4 9E 9B 9B 9B 9C 9F A4 A9 AE B2 B8 BF C7 10 13 F0 26 A8 A7 A8 AA AE B2 B7 BA C0 C7 CF BF
                                               B8 B1 AD AA A8 A7 AA AE B1 B6 BB C0 C5 CB D3 B8 AF A8 A3 9F 9C 9C A0 A4 A8 AE B4 B8 BE C5 CC B2
                                               A9 A1 9B 96 93 94 98 9C A1 60 02 B1 BE C5 AE A5 9D 96 90 8E 8E 92 97 6F 00 F1 2B B8 C1 AC A3 9A
                                               92 8C 88 89 8C 8D 91 97 9E A5 AC B5 BE A8 A0 97 8E 86 86 86 7E 86 8C 93 9C A4 AC B5 BE A5 9C 94
                                               89 84 81 7F 82 87 8F 97 9F A7 AF B7 C0 A4 9B 92 87 82 7F 86 8A 1F 01 F0 14 B4 BC C4 A5 9B 90 85
                                               7F 81 8D 96 9A 9F A5 AC B4 BC C3 CB A5 9B 91 85 80 80 8C 93 97 9C A3 AB B3 BA C1 C9 30 00 F0 27
                                               84 7F 83 87 8D 95 9C A4 AB B2 BA C3 A6 9D 94 8A 84 83 7E 7F 85 8D 95 9D A5 AD B6 BF A9 A1 98 8F
                                               87 85 88 84 83 8B 94 9C A4 AC B4 BD AC A4 9B 93 8D 8A 8A 8E 8C 8D 61 00 F0 6F B6 BE AF A6 9E 97
                                               92 8F 90 93 97 98 9E A6 AC B2 BA C2 B3 AA A2 9C 97 96 96 98 9D A3 A8 AE B3 B9 C0 C7 B9 B1 A9 A4
                                               A0 9F 9F A1 A5 AA B0 B6 BA C0 C7 CE C1 B9 B2 AE AB AB AA AC AF B3 B9 BE C2 C7 CD D5 C5 BE B8 B4
                                               B0 AE AE B0 B6 B8 BD C4 C9 CE D3 DB BE B7 B0 AB A7 A5 A5 A8 AD B1 B6 BC C1 C7 CD D4 B9 B1 AB A5
                                               A0 9E 9F A2 A6 AB AE B3 BA C0 C6 CE B6 AF A7 A0 9B 99 9A 9C 9E A2 A7 AD C0 00 F2 1A B4 AB A3 9B
                                               95 93 94 93 96 9B A0 A7 AF B6 BE C7 B0 A8 9E 96 92 8E 89 8C 90 96 9D A5 AD B5 BD C6 AE A5 9B 93
                                               8E 84 7A 86 8E 1F 01 80 C8 AD A2 98 91 88 7C 85 FD 01 00 1F 01 A0 CC AC A1 97 8F 84 7E 89 95 9E
                                               0F 00 F0 2C C3 CA D2 AD A1 97 90 85 7E 88 93 9B A3 AA B2 B9 C1 C8 D0 AD A3 99 91 89 7E 83 8C 93
                                               9B A3 AB B3 BB C2 CB AE A6 9B 94 90 87 85 86 8E 96 9E A6 AE B7 BF C7 B2 A9 9F 97 92 91 8C 8D 4D
                                               03 00 70 00 F0 53 B5 AC A4 9D 97 95 95 96 93 97 9D A7 B0 B7 BF C7 B7 AF A8 A1 9D 9A 9B 9E 9D 9E
                                               A3 AB B5 BB C2 CA BB B2 AB A6 A2 A0 A0 A3 A7 A8 AC B3 BB C1 C8 CF C0 B8 B1 AC A9 A7 A7 AA AD B2
                                               B6 BC C3 C9 CF D6 C6 BF B9 B4 B2 B1 B1 B2 B6 BA C0 C6 CB D0 D5 DC CC C6 C0 BC B8 B7 B7 B9 BE C2
                                               C6 CC D1 D6 DD E3 C6 C0 D2 06 F0 17 B0 B2 B6 BB BF C3 C9 CF D5 DC C2 BB B5 AF AC AA AA AD B0 B3
                                               B7 BC C2 C8 CF D6 C0 B8 B1 AA A6 A4 A4 A5 A7 AB B0 B6 D0 00 A0 BC B3 AB A5 A1 9E 9B 9C A0 A5 AE
                                               02 F0 1E C8 D0 B9 AF A7 A1 9A 95 94 95 9A A0 A7 AF B7 BF C7 CF B6 AC A4 9B 92 8E 90 92 98 A0 A8
                                               B0 B8 C1 C9 D1 B4 AA A2 98 8D 8A 8E 95 9B A4 AC 1F 01 F0 0D D4 B2 A8 A0 96 8F 8D 90 99 A3 AA B3
                                               BA C2 CA D2 D9 B3 A9 A0 96 90 8D 90 98 A1 A8 B0 4F 00 10 D8 30 00 F0 09 8E 8B 8D 94 9A A2 AA B3
                                               BB C3 CB D3 B6 AD A5 9D 94 8F 91 92 98 9F A7 AF 21 00 A1 BA B0 A8 A2 9C 97 95 97 9B 9F 2F 01 F0
                                               03 CF BD B5 AC A6 A2 A0 9E 9E 9E A1 A7 AF B8 C1 C8 D0 C1 61 05 F0 00 A6 A6 A7 A5 A7 AC B3 BB C4
                                               CB D3 C3 BC B5 B0 C3 08 F0 19 AE B0 B3 B9 C1 CA D1 D8 C8 C0 BA B6 B3 B1 B1 B4 B7 BA BC C1 C8 D1
                                               D7 DE CD C6 C1 BD BB B9 B9 BB BE C2 C7 CB D1 D9 DE E5
            """;

//        byte[] bytes = HexFormat.of().parseHex(hex.replaceAll("\\s+", ""));
//        byte[] result = Decompressor.lz4().decompress(bytes, 64 * 1024);

        Application.launch(MainWindow.class, args);
    }
}
