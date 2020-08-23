package com.evanandroid.apps.photodescription.ui.gallery

data class ContentList(var explation : String? = null, var imageUrl : String? = null, var uid : String? = null, var userId : String ? = null, var time: Long? = null)
{
    data class Comment(var uid: String? = null, var userId: String? = null, var comment: String? = null,var time: Long? = null)
    {

    }
}