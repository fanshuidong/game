package org.game.util.serializer;

import java.lang.reflect.Type;

public interface Serializer {

	byte[] serial(Object object);
	
	<ENTITY> ENTITY deserial(byte[] buffer, Type type);
	
	<ENTITY> ENTITY deserial(byte[] buffer, Class<ENTITY> clazz);
}
