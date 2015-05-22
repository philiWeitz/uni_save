

#include <Windows.h>
#include <iostream>

#include "org_uta_tcp_jni_KeyboardUtil.h"


JNIEXPORT jshortArray JNICALL Java_org_uta_tcp_jni_KeyboardUtil_getKeyboardState(JNIEnv *env, jclass) {
	short state[256] = { 0 };

	for (int i = 0; i < 256; ++i) 
	{
		state[i] = GetAsyncKeyState(i);
	}

	jshortArray result = env->NewShortArray(256);
	env->SetShortArrayRegion(result, 0, 256, reinterpret_cast<jshort*>(state));

	return result;
}