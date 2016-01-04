package kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper;

/**
 * Compress strategy.
 */
public interface Compressor {

    byte[] compress(byte[] bytes);
}
