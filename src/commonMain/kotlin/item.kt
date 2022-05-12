package fr.corli.teicorpo

// an item contains an transcription/annotation/coding single element

class Item {
    // - obligatory: id, name, type, parent, 'time-start', 'time-end', index, content
    // Obligatory elements: they need to exist otherwise the corpus data is not usable.
    // Either the time elements, or the index element, must have a non null value
    // id and ( code or name ) cannot have an empty value
    // type, parent, and content, can have empty values
    var id: String = ""
    var code: String = ""  // id for the coding
    var name: String = ""  // pointer to a person
    var type: String = ""
    var parent: String = ""
    var timestart: Double = -1.0
    var timeend: Double = -1.0
    var index: Int = -1
    var content: String = ""

    // - optional: list of first descendants, predecessor, follower, pointer to index table
    // Optional elements can be reconstructed from the obligatory elements. It is useful to have them for faster computation.
    var firstDescendants: Array<String>? = null
    var predecessor: String = ""
    var follower: String = ""
    var indexItems: MutableList<Item>? = null
}
