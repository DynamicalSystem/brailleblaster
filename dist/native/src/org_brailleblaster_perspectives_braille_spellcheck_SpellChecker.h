/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_brailleblaster_perspectives_braille_spellcheck_SpellChecker */

#ifndef _Included_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker
#define _Included_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_brailleblaster_perspectives_braille_spellcheck_SpellChecker
 * Method:    openDict
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_openDict
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     org_brailleblaster_perspectives_braille_spellcheck_SpellChecker
 * Method:    checkWord
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_checkWord
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_brailleblaster_perspectives_braille_spellcheck_SpellChecker
 * Method:    addWord
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_addWord
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_brailleblaster_perspectives_braille_spellcheck_SpellChecker
 * Method:    checkSug
 * Signature: (Ljava/lang/String;)Lorg/brailleblaster/perspectives/braille/spellcheck/Suggestions;
 */
JNIEXPORT jobject JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_checkSug
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_brailleblaster_perspectives_braille_spellcheck_SpellChecker
 * Method:    closeDict
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_closeDict
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif