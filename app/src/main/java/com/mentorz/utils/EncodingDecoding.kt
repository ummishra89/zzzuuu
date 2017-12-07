package com.mentorz.utils

/**
 * Created by craterzone on 27/07/17.
 */
object EncodingDecoding {
    private val UTF_8 = "UTF-8"

    /**
     * This method Encode String to UTF -8

     * @param stringToEncode
     * *
     * @return return Decoded String Or Same String if exception
     */
    fun encodeString(stringToEncode: String): String {
        var encodedString: String? = null
        try {
            encodedString = java.net.URLEncoder.encode(stringToEncode, UTF_8).replace("+", "%20")
        } catch (e: Exception) {
        }

        if (encodedString == null) {
            return stringToEncode
        }

        return encodedString
    }


    /**
     * This method decode String from UTF -8

     * @param stringToEncode
     * *
     * @return return Decoded String Or Same String if exception
     */
    fun decodeString(stringToDecode: String): String {
        var decodedString: String? = null
        try {
            decodedString = java.net.URLDecoder.decode(stringToDecode, UTF_8)
        } catch (e: Exception) {
        }

        if (decodedString == null) {
            return stringToDecode
        }
        return decodedString
    }
}