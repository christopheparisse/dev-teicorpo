package fr.corli.teicorpo

// METADATA not related to the content of the data
// metadata about the authors, creators, rights, software, location of data, etc.
enum class TypeResp {
    ORGANISM, PROJECT, PERSON, OTHER
}

enum class Function {
    annotator, compiler, interviewer, recorder, transcriber, translator, data_inputter, OTHER
}

class Principal(val principal: String) {
    var name: String = ""
    var type: TypeResp = TypeResp.OTHER
}

class Responsible(val resp: String) {
    var name: String = ""
    var function: Function = Function.OTHER
    var type: TypeResp = TypeResp.OTHER
}

class DocumentData {
    var title: String = "" // Title, description, citation
    var shortTitle: String = "" // Short Description
    var bibl: MutableList<String> = mutableListOf<String>() // Citation(s) : project, research team, scientific reference
    var principal: MutableList<Principal> = mutableListOf<Principal>()
    var resp: MutableList<Responsible> = mutableListOf<Responsible>()

    // missing from TeiMeta
    // publicationData
    // textData

}

class Media(val nature: String, val format: String) {
    var duration: Double = -1.0
    var quality: String = ""
    var anonymization: String = ""
    var url: String = ""
}

// METADATA about the content of the data
// information about media files used during recordings for the data
class MediaData {
    // corresponds to source in TEI
    var media: MutableList<Media> = mutableListOf()
}

class Place(val place: String) {
    var country: String = ""
}

class Setting(val activity: String) {
    var date: String = ""
}

// information about the place of the recording
class SettingData {
    var place: MutableList<Place> = mutableListOf<Place>()
    var langUse: MutableList<String> = mutableListOf<String>()
    var setting: MutableList<Setting> = mutableListOf<Setting>()
}

// information about a language and its position (1st language, 2nd language, etc.) and its quantity of use
class Language(var name: String) {
    var use: String = "1st"
    var percent: Int = 100
}

// information about people recorded
class PersonData {
    var name: String = ""
    var id: String = ""
    var lang: MutableList<Language> = mutableListOf<Language>()
    var age: String = ""
    var ses: String = ""
    var educ: String = ""
    var sex: String = ""
    var occupation: String = ""
    var note: String = ""
}

enum class Type {
    DEFAULT, TIMEDIV, INCLUDED, SUBDIVISION, ASSOCIATION, POINT
}

enum class ItemFormat {
    DEFAULT, CHAT, TRANSCRIBER, CV, TEI
}

// TEMPLATE INFORMATION: structure of the text
// types of items used
class TypeData {
    var name: String = ""
    var type: Type = Type.DEFAULT
    var cv: MutableList<String> = mutableListOf<String>()
    var format: ItemFormat = ItemFormat.DEFAULT
}
