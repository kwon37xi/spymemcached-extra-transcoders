package kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper;

/**
 * Decompress strategy
 */
public interface Decompressor {
    byte[] decompress(byte[] bytes);
}
