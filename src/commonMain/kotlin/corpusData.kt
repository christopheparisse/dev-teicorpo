package fr.corli.teicorpo

// the corpusData contains a full transcription/annotation/coding of a file

class Div {
    var startIndex: Int = -1
    var endIndex: Int = -1
    var startTime: Double = -1.0
    var endTime: Double = -1.0
    var type: String = ""
    var description: String = ""
}

class CorpusData {
    // METADATA not related to the content of the data
    // metadata about the authors, creators, rights, software, location of data, etc.
    var documentData: DocumentData = DocumentData()

    // METADATA about the content of the data
    // information about media files used during recordings for the data
    var mediaData: MutableList<MediaData> = mutableListOf()
    // information about the place of the recording
    var settingData: SettingData = SettingData()
    // information about people recorded
    var personsData: MutableList<PersonData> = mutableListOf()

    // TEMPLATE INFORMATION: structure of the text
    // names of the tiers
    var namesData: MutableList<String> = mutableListOf()
    // types of items used
    var typesData: MutableList<TypeData> = mutableListOf()

    // THE DATA
    // the content of the transcription/coding/annotation
    var text: MutableList<Item> = mutableListOf()
    // the organisation of the transcription/coding/annotation in subparts
    var divs: MutableList<Div> = mutableListOf()
    // is the metric of the transcription/coding/annotation index based or time based
    // index correspond to sequential information
    // it cannot be only time based if some items do not have time in them
    var indexBased: Boolean = true
}
