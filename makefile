CC=gcc
JAVA_HOME=$(shell echo $$JAVA_HOME)

reactivepi:  
	mkdir -p target/native
	$(CC) -o target/native/reactivepi.so -I"$(JAVA_HOME)/include" \
		-I"$(JAVA_HOME)/include/linux" -shared src/jni/nl_fizzylogic_reactivepi_i2c_I2CDevice.c
