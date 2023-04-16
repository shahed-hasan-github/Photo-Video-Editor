/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package infiapp.com.videomaker.song;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.HashMap;

public class AACodec extends SoundFile {
    public static Factory getFactory() {
        return new Factory() {
            public SoundFile create() {
                return new AACodec();
            }

            public String[] getSupportedExtensions() {
                return new String[]{"aac", "m4a"};
            }
        };
    }

    public class Atom {
        int start;
        int len;  // including header
        byte[] data;
    }


    public static final int KDINF = 0x64696e66;
    public static final int KFTYP = 0x66747970;
    public static final int KHDLR = 0x68646c72;
    public static final int KMDAT = 0x6d646174;
    public static final int KMDHD = 0x6d646864;
    public static final int KMDIA = 0x6d646961;
    public static final int KMINF = 0x6d696e66;
    public static final int KMOOV = 0x6d6f6f76;
    public static final int KMP4A = 0x6d703461;
    public static final int KMVHD = 0x6d766864;
    public static final int KSMHD = 0x736d6864;
    public static final int KSTBL = 0x7374626c;
    public static final int KSTCO = 0x7374636f;
    public static final int KSTSC = 0x73747363;
    public static final int KSTSD = 0x73747364;
    public static final int KSTSZ = 0x7374737a;
    public static final int KSTTS = 0x73747473;
    public static final int KTKHD = 0x746b6864;
    public static final int KTRAK = 0x7472616b;

    protected int[] kRequiredAtoms = {
            KDINF,
            KHDLR,
            KMDHD,
            KMDIA,
            KMINF,
            KMOOV,
            KMVHD,
            KSMHD,
            KSTBL,
            KSTSD,
            KSTSZ,
            KSTTS,
            KTKHD,
            KTRAK,
    };

    protected int[] kSaveDataAtoms = {
            KDINF,
            KHDLR,
            KMDHD,
            KMVHD,
            KSMHD,
            KTKHD,
            KSTSD,
    };

    // Member variables containing frame info
    private int mNumFrames;
    private int[] mFrameOffsets;
    private int[] mFrameLens;
    private int[] mFrameGains;
    private int mFileSize;
    private HashMap<Integer, Atom> mAtomMap;


    private int mSampleRate;
    private int mChannels;
    private int mSamplesPerFrame;

    // Member variables used only while initially parsing the file
    private int mOffset;
    private int mMinGain;
    private int mMaxGain;
    private int mMdatOffset;
    private int mMdatLength;

    public AACodec() {
        // Do nothing because of X and Y.
    }

    public int getNumFrames() {
        return mNumFrames;
    }

    public int getSamplesPerFrame() {
        return mSamplesPerFrame;
    }

    public int[] getFrameOffsets() {
        return mFrameOffsets;
    }

    public int[] getFrameLens() {
        return mFrameLens;
    }

    public int[] getFrameGains() {
        return mFrameGains;
    }

    public int getFileSizeBytes() {
        return mFileSize;
    }

    public int getAvgBitrateKbps() {
        return mFileSize / (mNumFrames * mSamplesPerFrame);
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public int getChannels() {
        return mChannels;
    }


    public String atomToString(int atomType) {
        String str = "";
        str += (char) ((atomType >> 24) & 0xff);
        str += (char) ((atomType >> 16) & 0xff);
        str += (char) ((atomType >> 8) & 0xff);
        str += (char) (atomType & 0xff);
        return str;
    }

    public void readFile(File inputFile)
            throws java.io.FileNotFoundException,
            java.io.IOException {
        super.readFile(inputFile);
        mChannels = 0;
        mSampleRate = 0;
        mSamplesPerFrame = 0;
        mNumFrames = 0;
        mMinGain = 255;
        mMaxGain = 0;
        mOffset = 0;
        mMdatOffset = -1;
        mMdatLength = -1;

        mAtomMap = new HashMap();

        // No need to handle filesizes larger than can fit in a 32-bit int
        mFileSize = (int) mInputFile.length();

        if (mFileSize < 128) {
            throw new java.io.IOException("File too small to parse");
        }

        // Read the first 8 bytes
        FileInputStream stream = new FileInputStream(mInputFile);
        byte[] header = new byte[8];
        stream.read(header, 0, 8);

        if (header[0] == 0 &&
                header[4] == 'f' &&
                header[5] == 't' &&
                header[6] == 'y' &&
                header[7] == 'p') {
            // Create a new stream, reset to the beginning of the file
            stream = new FileInputStream(mInputFile);
            parseMp4(stream, mFileSize);
        } else {
            throw new java.io.IOException("Unknown file format");
        }

        if (mMdatOffset > 0 && mMdatLength > 0) {
            stream = new FileInputStream(mInputFile);
            stream.skip(mMdatOffset);
            mOffset = mMdatOffset;
            parseMdat(stream, mMdatLength);
        } else {
            throw new java.io.IOException("Didn't find mdat");
        }


        boolean bad = false;
        for (int requiredAtomType : kRequiredAtoms) {
            if (!mAtomMap.containsKey(requiredAtomType)) {

                bad = true;
            }
        }

        if (bad) {
            throw new java.io.IOException("Could not parse MP4 file");
        }
    }

    private void parseMp4(InputStream stream, int maxLen)
            throws java.io.IOException {


        while (maxLen > 8) {
            int initialOffset = mOffset;

            byte[] atomHeader = new byte[8];
            stream.read(atomHeader, 0, 8);
            int atomLen =
                    ((0xff & atomHeader[0]) << 24) |
                            ((0xff & atomHeader[1]) << 16) |
                            ((0xff & atomHeader[2]) << 8) |
                            (0xff & atomHeader[3]);

            if (atomLen > maxLen)
                atomLen = maxLen;
            int atomType =
                    ((0xff & atomHeader[4]) << 24) |
                            ((0xff & atomHeader[5]) << 16) |
                            ((0xff & atomHeader[6]) << 8) |
                            (0xff & atomHeader[7]);

            Atom atom = new Atom();
            atom.start = mOffset;
            atom.len = atomLen;
            mAtomMap.put(atomType, atom);

            mOffset += 8;

            if (atomType == KMOOV ||
                    atomType == KTRAK ||
                    atomType == KMDIA ||
                    atomType == KMINF ||
                    atomType == KSTBL) {
                parseMp4(stream, atomLen);
            } else if (atomType == KSTSZ) {
                parseStsz(stream);
            } else if (atomType == KSTTS) {
                parseStts(stream);
            } else if (atomType == KMDAT) {
                mMdatOffset = mOffset;
                mMdatLength = atomLen - 8;
            } else {
                for (int savedAtomType : kSaveDataAtoms) {
                    if (savedAtomType == atomType) {
                        byte[] data = new byte[atomLen - 8];
                        stream.read(data, 0, atomLen - 8);
                        mOffset += atomLen - 8;
                        mAtomMap.get(atomType).data = data;
                    }
                }
            }

            if (atomType == KSTSD) {
                parseMp4aFromStsd();
            }

            maxLen -= atomLen;
            int skipLen = atomLen - (mOffset - initialOffset);


            if (skipLen < 0) {
                throw new java.io.IOException(
                        "Went over by " + (-skipLen) + " bytes");
            }

            stream.skip(skipLen);
            mOffset += skipLen;
        }
    }

    void parseStts(InputStream stream)
            throws java.io.IOException {
        byte[] sttsData = new byte[16];
        stream.read(sttsData, 0, 16);
        mOffset += 16;
        mSamplesPerFrame =
                ((0xff & sttsData[12]) << 24) |
                        ((0xff & sttsData[13]) << 16) |
                        ((0xff & sttsData[14]) << 8) |
                        (0xff & sttsData[15]);

    }

    void parseStsz(InputStream stream)
            throws java.io.IOException {
        byte[] stszHeader = new byte[12];
        stream.read(stszHeader, 0, 12);
        mOffset += 12;
        mNumFrames =
                ((0xff & stszHeader[8]) << 24) |
                        ((0xff & stszHeader[9]) << 16) |
                        ((0xff & stszHeader[10]) << 8) |
                        (0xff & stszHeader[11]);

        mFrameOffsets = new int[mNumFrames];
        mFrameLens = new int[mNumFrames];
        mFrameGains = new int[mNumFrames];
        byte[] frameLenBytes = new byte[4 * mNumFrames];
        stream.read(frameLenBytes, 0, 4 * mNumFrames);
        mOffset += 4 * mNumFrames;
        for (int i = 0; i < mNumFrames; i++) {
            mFrameLens[i] =
                    ((0xff & frameLenBytes[4 * i + 0]) << 24) |
                            ((0xff & frameLenBytes[4 * i + 1]) << 16) |
                            ((0xff & frameLenBytes[4 * i + 2]) << 8) |
                            (0xff & frameLenBytes[4 * i + 3]);
        }
    }

    void parseMp4aFromStsd() {
        byte[] stsdData = mAtomMap.get(KSTSD).data;
        mChannels =
                ((0xff & stsdData[32]) << 8) |
                        (0xff & stsdData[33]);
        mSampleRate =
                ((0xff & stsdData[40]) << 8) |
                        (0xff & stsdData[41]);

    }

    void parseMdat(InputStream stream, int maxLen)
            throws java.io.IOException {
        int initialOffset = mOffset;
        for (int i = 0; i < mNumFrames; i++) {
            mFrameOffsets[i] = mOffset;


            if (mOffset - initialOffset + mFrameLens[i] > maxLen - 8) {
                mFrameGains[i] = 0;
            } else {
                readFrame(stream, i);
            }
            if (mFrameGains[i] < mMinGain)
                mMinGain = mFrameGains[i];
            if (mFrameGains[i] > mMaxGain)
                mMaxGain = mFrameGains[i];

            if (mProgressListener != null) {
                boolean keepGoing = mProgressListener.reportProgress(
                        mOffset * 1.0 / mFileSize);
                if (!keepGoing) {
                    break;
                }
            }
        }
    }

    void readFrame(InputStream stream, int frameIndex)
            throws java.io.IOException {

        if (mFrameLens[frameIndex] < 4) {
            mFrameGains[frameIndex] = 0;
            stream.skip(mFrameLens[frameIndex]);
            return;
        }

        int initialOffset = mOffset;

        byte[] data = new byte[4];
        stream.read(data, 0, 4);
        mOffset += 4;


        int idSynEle = (0xe0 & data[0]) >> 5;

        switch (idSynEle) {
            case 0:  // ID_SCE: mono
                int monoGain = ((0x01 & data[0]) << 7) | ((0xfe & data[1]) >> 1);
                mFrameGains[frameIndex] = monoGain;
                break;
            case 1:  // ID_CPE: stereo
                int windowSequence = (0x60 & data[1]) >> 5;

                int maxSfb;
                int scaleFactorGrouping;
                int maskPresent;
                int startBit;

                if (windowSequence == 2) {
                    maxSfb = 0x0f & data[1];

                    scaleFactorGrouping = (0xfe & data[2]) >> 1;

                    maskPresent =
                            ((0x01 & data[2]) << 1) |
                                    ((0x80 & data[3]) >> 7);

                    startBit = 25;
                } else {
                    maxSfb =
                            ((0x0f & data[1]) << 2) |
                                    ((0xc0 & data[2]) >> 6);

                    scaleFactorGrouping = -1;

                    maskPresent = (0x18 & data[2]) >> 3;

                    startBit = 21;
                }


                if (maskPresent == 1) {
                    int sfgZeroBitCount = 0;
                    for (int b = 0; b < 7; b++) {
                        if ((scaleFactorGrouping & (1 << b)) == 0) {

                            sfgZeroBitCount++;
                        }
                    }

                    int numWindowGroups = 1 + sfgZeroBitCount;

                    int skip = maxSfb * numWindowGroups;

                    startBit += skip;
                }

                // We may need to fill our buffer with more than the 4
                // bytes we've already read, here.
                int bytesNeeded = 1 + ((startBit + 7) / 8);
                byte[] oldData = data;
                data = new byte[bytesNeeded];
                data[0] = oldData[0];
                data[1] = oldData[1];
                data[2] = oldData[2];
                data[3] = oldData[3];
                stream.read(data, 4, bytesNeeded - 4);
                mOffset += (bytesNeeded - 4);

                int firstChannelGain = 0;
                for (int b = 0; b < 8; b++) {
                    int b0 = (b + startBit) / 8;
                    int b1 = 7 - ((b + startBit) % 8);
                    int add = (((1 << b1) & data[b0]) >> b1) << (7 - b);

                    firstChannelGain += add;
                }

                mFrameGains[frameIndex] = firstChannelGain;
                break;

            default:
                if (frameIndex > 0) {
                    mFrameGains[frameIndex] = mFrameGains[frameIndex - 1];
                } else {
                    mFrameGains[frameIndex] = 0;
                }
                break;
        }

        int skip = mFrameLens[frameIndex] - (mOffset - initialOffset);


        stream.skip(skip);
        mOffset += skip;
    }

    public void startAtom(FileOutputStream out, int atomType)
            throws java.io.IOException {
        byte[] atomHeader = new byte[8];
        int atomLen = mAtomMap.get(atomType).len;
        atomHeader[0] = (byte) ((atomLen >> 24) & 0xff);
        atomHeader[1] = (byte) ((atomLen >> 16) & 0xff);
        atomHeader[2] = (byte) ((atomLen >> 8) & 0xff);
        atomHeader[3] = (byte) (atomLen & 0xff);
        atomHeader[4] = (byte) ((atomType >> 24) & 0xff);
        atomHeader[5] = (byte) ((atomType >> 16) & 0xff);
        atomHeader[6] = (byte) ((atomType >> 8) & 0xff);
        atomHeader[7] = (byte) (atomType & 0xff);
        out.write(atomHeader, 0, 8);
    }

    public void writeAtom(FileOutputStream out, int atomType)
            throws java.io.IOException {
        Atom atom = mAtomMap.get(atomType);
        startAtom(out, atomType);
        out.write(atom.data, 0, atom.len - 8);
    }

    public void setAtomData(int atomType, byte[] data) {
        Atom atom = mAtomMap.get(atomType);
        if (atom == null) {
            atom = new Atom();
            mAtomMap.put(atomType, atom);
        }
        atom.len = data.length + 8;
        atom.data = data;
    }

    public void writeFile(File outputFile, int startFrame, int numFrames)
            throws java.io.IOException {
        outputFile.createNewFile();
        FileOutputStream out;
        try (FileInputStream in = new FileInputStream(mInputFile)) {
            out = new FileOutputStream(outputFile);

            setAtomData(KFTYP, new byte[]{
                    'M', '4', 'A', ' ',
                    0, 0, 0, 0,
                    'M', '4', 'A', ' ',
                    'm', 'p', '4', '2',
                    'i', 's', 'o', 'm',
                    0, 0, 0, 0
            });

            setAtomData(KSTTS, new byte[]{
                    0, 0, 0, 0,  // version / flags
                    0, 0, 0, 1,  // entry count
                    (byte) ((numFrames >> 24) & 0xff),
                    (byte) ((numFrames >> 16) & 0xff),
                    (byte) ((numFrames >> 8) & 0xff),
                    (byte) (numFrames & 0xff),
                    (byte) ((mSamplesPerFrame >> 24) & 0xff),
                    (byte) ((mSamplesPerFrame >> 16) & 0xff),
                    (byte) ((mSamplesPerFrame >> 8) & 0xff),
                    (byte) (mSamplesPerFrame & 0xff)
            });

            setAtomData(KSTSC, new byte[]{
                    0, 0, 0, 0,  // version / flags
                    0, 0, 0, 1,  // entry count
                    0, 0, 0, 1,  // first chunk
                    (byte) ((numFrames >> 24) & 0xff),
                    (byte) ((numFrames >> 16) & 0xff),
                    (byte) ((numFrames >> 8) & 0xff),
                    (byte) (numFrames & 0xff),
                    0, 0, 0, 1  // Smaple desc index
            });

            byte[] stszData = new byte[12 + 4 * numFrames];
            stszData[8] = (byte) ((numFrames >> 24) & 0xff);
            stszData[9] = (byte) ((numFrames >> 16) & 0xff);
            stszData[10] = (byte) ((numFrames >> 8) & 0xff);
            stszData[11] = (byte) (numFrames & 0xff);
            for (int i = 0; i < numFrames; i++) {
                stszData[12 + 4 * i] =
                        (byte) ((mFrameLens[startFrame + i] >> 24) & 0xff);
                stszData[13 + 4 * i] =
                        (byte) ((mFrameLens[startFrame + i] >> 16) & 0xff);
                stszData[14 + 4 * i] =
                        (byte) ((mFrameLens[startFrame + i] >> 8) & 0xff);
                stszData[15 + 4 * i] =
                        (byte) (mFrameLens[startFrame + i] & 0xff);
            }
            setAtomData(KSTSZ, stszData);

            int mdatOffset =
                    144 +
                            4 * numFrames +
                            mAtomMap.get(KSTSD).len +
                            mAtomMap.get(KSTSC).len +
                            mAtomMap.get(KMVHD).len +
                            mAtomMap.get(KTKHD).len +
                            mAtomMap.get(KMDHD).len +
                            mAtomMap.get(KHDLR).len +
                            mAtomMap.get(KSMHD).len +
                            mAtomMap.get(KDINF).len;


            setAtomData(KSTCO, new byte[]{
                    0, 0, 0, 0,  // version / flags
                    0, 0, 0, 1,  // entry count
                    (byte) ((mdatOffset >> 24) & 0xff),
                    (byte) ((mdatOffset >> 16) & 0xff),
                    (byte) ((mdatOffset >> 8) & 0xff),
                    (byte) (mdatOffset & 0xff),
            });

            mAtomMap.get(KSTBL).len =
                    8 +
                            mAtomMap.get(KSTSD).len +
                            mAtomMap.get(KSTTS).len +
                            mAtomMap.get(KSTSC).len +
                            mAtomMap.get(KSTSZ).len +
                            mAtomMap.get(KSTCO).len;

            mAtomMap.get(KMINF).len =
                    8 +
                            mAtomMap.get(KDINF).len +
                            mAtomMap.get(KSMHD).len +
                            mAtomMap.get(KSTBL).len;

            mAtomMap.get(KMDIA).len =
                    8 +
                            mAtomMap.get(KMDHD).len +
                            mAtomMap.get(KHDLR).len +
                            mAtomMap.get(KMINF).len;

            mAtomMap.get(KTRAK).len =
                    8 +
                            mAtomMap.get(KTKHD).len +
                            mAtomMap.get(KMDIA).len;

            mAtomMap.get(KMOOV).len =
                    8 +
                            mAtomMap.get(KMVHD).len +
                            mAtomMap.get(KTRAK).len;

            int mdatLen = 8;
            for (int i = 0; i < numFrames; i++) {
                mdatLen += mFrameLens[startFrame + i];
            }
            mAtomMap.get(KMDAT).len = mdatLen;

            writeAtom(out, KFTYP);
            startAtom(out, KMOOV);
            writeAtom(out, KMVHD);
            startAtom(out, KTRAK);
            writeAtom(out, KTKHD);
            startAtom(out, KMDIA);
            writeAtom(out, KMDHD);
            writeAtom(out, KHDLR);
            startAtom(out, KMINF);
            writeAtom(out, KDINF);
            writeAtom(out, KSMHD);
            startAtom(out, KSTBL);
            writeAtom(out, KSTSD);
            writeAtom(out, KSTTS);
            writeAtom(out, KSTSC);
            writeAtom(out, KSTSZ);
            writeAtom(out, KSTCO);
            startAtom(out, KMDAT);

            int maxFrameLen = 0;
            for (int i = 0; i < numFrames; i++) {
                if (mFrameLens[startFrame + i] > maxFrameLen)
                    maxFrameLen = mFrameLens[startFrame + i];
            }
            byte[] buffer = new byte[maxFrameLen];
            int pos = 0;
            for (int i = 0; i < numFrames; i++) {
                int skip = mFrameOffsets[startFrame + i] - pos;
                int len = mFrameLens[startFrame + i];
                if (skip < 0) {
                    continue;
                }
                if (skip > 0) {
                    in.skip(skip);
                    pos += skip;
                }
                in.read(buffer, 0, len);
                out.write(buffer, 0, len);
                pos += len;
            }

            in.close();
        }
        out.close();
    }


}
