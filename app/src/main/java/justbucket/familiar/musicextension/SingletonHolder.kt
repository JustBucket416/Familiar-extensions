package justbucket.familiar.musicextension

open class SingletonHolder<out T : Any, in A>(private val creator: (A) -> T) {
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A?): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator(arg as A)
                instance = created
                created
            }
        }
    }
}