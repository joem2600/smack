package me.joemacdonald.smack.Utilities


const val BASE_URL = "http://10.0.2.2:3005/v1/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_ADD = "${BASE_URL}user/add"

const val TAG = "JMAC"

/** The default socket timeout in milliseconds  */
val DEFAULT_TIMEOUT_MS = 30000

/** The default number of retries  */
val DEFAULT_MAX_RETRIES = 2

/** The default backoff multiplier  */
val DEFAULT_BACKOFF_MULT = 1f