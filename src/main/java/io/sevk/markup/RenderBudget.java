package io.sevk.markup;

final class RenderBudget {
    static final int MAX_MARKUP_DEPTH = 64;
    static final int MAX_TAG_ITERATIONS = 200;
    static final int MAX_RENDER_OUTPUT = 5_000_000;
    static final long MAX_TAG_SCAN_WORK = 30_000_000L;
    static final int MAX_BLOCK_LENGTH = 2500;
    static final int MAX_TOTAL_LENGTH = 2500;
    static final String TOO_COMPLEX = "Email markup is too complex to render";

    private long scanWork;
    private int highlighted;

    void chargeScanWork(long work) {
        scanWork += work;
        if (scanWork > MAX_TAG_SCAN_WORK) {
            throw new IllegalArgumentException(TOO_COMPLEX);
        }
    }

    boolean admits(int length) {
        return length <= MAX_BLOCK_LENGTH && highlighted + length <= MAX_TOTAL_LENGTH;
    }

    void charge(int length) {
        highlighted += length;
    }
}
