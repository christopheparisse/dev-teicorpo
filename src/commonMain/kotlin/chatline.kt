package fr.corli.teicorpo

/**
 * @author Christophe Parisse
 * charge un fichier CHAT
 * fonctions utilitaires pour accéder aux données
 *
 * usage principal: initialise une structure de données qui permet de balayer et tester toutes les lignes de CHAT
 * les informations de métadonnées (champs Participants et ID sont analysées et extraites séparement
 * des variables spécifiques sont crées pour contenir ces données (voir ID et chatFilename, mediaFilename,
 * mediaType, birth, date, location, transcriber)
 * nbMainLines() : nombre de lignes principales
 * ml(i) accès à la ligne principale de numéro 'i' (toutes les lignes y compris les \@)
 * mlc(i) accès à la ligne principale nettoyée de numéro 'i' (toutes les lignes y compris les \@)
 * startMl(i) : timecode de début de la ligne 'i'
 * endMl(i) : timecode de fin de la ligne 'i'
 * nbTiers(i) : nombre de tiers de la ligne 'i'
 * t(i,j) : tiers 'j' de la ligne 'i'
 */

class ChatLine(line: String) {
    var head: String? = ""
    var tail: String? = ""

    init {
        if (line == null || line.isEmpty()) {
            head = ""
            tail = ""
        } else {
            val pattern = Regex("([%*@][A-Za-zÀ-ÖØ-öø-ÿ_0-9_-]*)[\\s:]+(.*)")
            //System.out.println(line);
            val matchResult = pattern.find(line)
            if (matchResult == null) { // cas des lignes avec @ mais sans :
                head = line
                tail = ""
                println("xx> $head");
            } else {
                if (matchResult.groupValues.size == 2) { // cas des lignes avec @ ou * et un :
                    val (who, content) = matchResult!!.destructured
                    head = who
                    if (head!!.length == 1) {
                        head += "UNK"
                    }
                    tail = content
                    println("x> $head <x> $tail");
                } else {
                    println("Error: Chatline length=1 x> $line")
                }
            }
        }
    }

    fun type(): String? {
        return head
    }

    override fun toString(): String {
        return "who[$head]u[$tail]"
    }

    companion object {
        fun main(args: Array<String>) {
            val cl = ChatLine(args[0])
            println(cl.head + " :-: " + cl.tail)
        }
    }
}
