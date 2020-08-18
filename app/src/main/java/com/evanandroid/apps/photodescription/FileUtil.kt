package com.evanandroid.apps.photodescription

import java.io.*

class FileUtil() {
    fun readTextFile(fullPath:String):String{
        val file = File(fullPath)
        if(!file.exists()) return ""// 파일이 없는 경우 공백 값을 리턴
        val reader = FileReader(file) // 파일을 읽는 코드
        val buffer = BufferedReader(reader) //버퍼를 통해 속도를 향상
        var temp = ""
        val result = StringBuffer() // temp로 한 줄씩 읽은 내용을 모두 저장하는 전체 저장 버퍼
        while (true){
            temp = buffer.readLine()
            if(temp == null) break
            else result.append(buffer)
        }
        buffer.close()
        return result.toString()
    }

    fun writeTextFile(directory:String, filename : String, content: String){
        val dir = File(directory)
        if(!dir.exists()){
            dir.mkdirs()
        }
        val writer = FileWriter(directory + "/" + filename)
        val buffer = BufferedWriter(writer)
        buffer.write(content)
        buffer.close()
    }
}