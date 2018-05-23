package wc

import java.io.File
import java.util.*
import kotlin.collections.*
import kotlin.concurrent.thread

var word_space : Queue<String> =ArrayDeque<String>()
var freq_space : Queue<HashMap<String,Int>> = ArrayDeque<HashMap<String,Int>>()
var stopwords : Set<String> = File("/home/diego/Documentos/tp2/WC/src/main/resources/stop_words.txt").readLines().toString().split(",").toSet()


fun process_words(){
    val word_freqs = hashMapOf<String,Int>()
    while (true){
        var word : String
        try{
            word = word_space.element()
            word_space.remove()
        }
        catch (e : java.util.NoSuchElementException){
            break
        }
        if(!stopwords.contains(word)){
            if(word_freqs.containsKey(word)){
                word_freqs[word] =(1 + word_freqs[word]!!.toInt())
            }
            else{
                word_freqs[word] = 1
            }
        }
    }
    freq_space.add(word_freqs)
}

fun main(args : Array<String>){
    val argv = File("/home/diego/Documentos/pride-and-prejudice.txt").readLines().toString().toLowerCase()
    val re = Regex("""[a-z]{2,}""").findAll(argv)
    for(word in re){
        word_space.add(word.value)
    }
    val workers = Array(5,{ thread { process_words() }})

    workers.forEach { it.join() }

    val word_freqs = hashMapOf<String,Int>()
    var count : Int
    while (freq_space.isNotEmpty()){
        val freqs  = freq_space.element()
        freq_space.remove()
        for(tuple  in freqs ){
            if(word_freqs.containsKey(tuple.key)){
                count = freqs[tuple.key]!!.toInt()
               count += word_freqs[tuple.key]!!.toInt()
            }
            else{
                count = freqs[tuple.key]!!.toInt()
            }
            word_freqs[tuple.key] = count
        }
    }
    var c = 0
    val result = word_freqs.toList().sortedBy { (_,value)-> value }.reversed().toMap()
    for(tuple in result) {
        if(c<25){
            println("${tuple.key} - ${tuple.value}")
        }
        c++
    }
}