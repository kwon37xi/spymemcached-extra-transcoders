package kr.pe.kwonnam.spymemcached.extratranscoders.fst;

import org.nustaq.serialization.FSTConfiguration;

/**
 * {@link org.nustaq.serialization.FSTConfiguration} instance factory.
 */
public interface FSTConfigurationFactory {
    FSTConfiguration create();
}
