package io.mercury.persistence.avro.serializable;

import org.apache.avro.specific.SpecificDatumReader;
import org.slf4j.Logger;

import io.ffreedom.common.log.CommonLoggerFactory;

public abstract class BaseAvroDeserializer<T> {

	protected Logger logger = CommonLoggerFactory.getLogger(getClass());

	private SpecificDatumReader<T> datumReader;
	
	protected final SpecificDatumReader<T> getDatumReader(Class<T> tClass) {
		if(datumReader == null) {
			datumReader = initDatumReader(tClass);
		}
		return datumReader;
	}

	private final SpecificDatumReader<T> initDatumReader(Class<T> tClass) {
		return new SpecificDatumReader<>(tClass);
	}

}