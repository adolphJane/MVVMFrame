package com.magicalrice.project.library_data

object BaseConstants {
    const val GLOBAL_BROADCAST_ACTION = "GLOBAL_BROADCAST"
    const val TOKEN_UNAUTHORIZED = "TOKEN_UNAUTHORIZED"

    /**
     * 富文本适配
     */
    fun getHtmlData(bodyHTML: String): String {
        val head = ("<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>")
        return "<html>$head<body>$bodyHTML</body></html>"
    }
}