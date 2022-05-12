package fr.corli.teicorpo

class ChatFile internal constructor() {
    var wellFormed = true

    inner class ID {
        var code: String? = null
        var name: String? = null
        var role: String? = null

        // dans l'@ID
        var language: String? = null
        var corpus: String? = null

        //code;
        var age: String? = null
        var sex: String? = null
        var group: String? = null
        var SES: String? = null

        // role;
        var education: String? = null
        var customfield: String? = null
    }

    var chatFilename: String? = null
    var mediaFilename: String? = null
    var mediaType: String? = null
    var birth: String? = null
    var date: String? = null
    var location: String? = null
    var transcriber: String? = null
    var situation: String? = null
    var lang: Array<String>
    var timeDuration: String? = null
    var timeStart: String? = null
    var comments: java.util.ArrayList<String> = java.util.ArrayList<String>()
    var gemes: java.util.ArrayList<String> = java.util.ArrayList<String>()
    var otherInfo: java.util.ArrayList<String> = java.util.ArrayList<String>()
    var ids: java.util.ArrayList<ID>? = java.util.ArrayList<ID>()
    var inMainLine = false
    fun findInfo(verbose: Boolean, tparams: TierParams) {
        if (!tparams.inputFormat.equals(".cha")) return
        // find all types of information and preprocess it.
        val sz = nbMainLines()
        var inHeader = true
        val idsMap: MutableSet<String> = java.util.HashSet<String>()
        for (i in 0 until sz) {
            if (ml(i)!!.startsWith("*")) {
                inHeader = false
                val cl = ChatLine(ml(i))
                // System.out.printf("== * (%s)(%s)%n", cl.head, cl.tail);
                if (cl.head.length() > 1) {
                    var code: String = cl.head.substring(1)
                    if (tparams.target.equals("dinlang")) {
                        // if in a list of special name, replace it
                        val oc = code
                        // System.out.printf("IN *%n");
                        code = equivalence(code, tparams)
                        // System.out.printf("Changed (%s) --> (%s) %n", oc, code);
                    }
                    if (!idsMap.contains(code)) {
                        idsMap.add(code)
                        val nid: ID = ID()
                        nid.code = code
                        if (nid.code!!.isEmpty()) nid.code = "UNK"
                        if (nid.code == "UNK") nid.role = "Unknown"
                        ids.add(nid)
                    }
                }
            }
            if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@participants")) {
                val rls = ml(i)!!.split("[:,]+".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                for (k in 1 until rls.size) {
                    val wds = rls[k].trim { it <= ' ' }.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    var trueCode = wds[0]
                    if (tparams.target.equals("dinlang")) {
                        // if in a list of special name, replace it
                        val oc = trueCode
                        // System.out.printf("IN @participants%n");
                        trueCode = equivalence(trueCode, tparams)
                        // System.out.printf("Part Changed (%s) --> (%s) %n", oc, trueCode);
                    }
                    // System.out.printf("L=%s='%s'/'%s'%n", rls[k], wds[0], trueCode);
                    if (wds.size == 2) {
                        val nid: ID = ID()
                        nid.code = trueCode
                        nid.role = wds[1]
                        ids.add(nid)
                        idsMap.add(trueCode)
                        // System.out.printf("ID2: %s %s%n", nid.code, nid.role);
                    } else if (wds.size == 3) {
                        val nid: ID = ID()
                        nid.code = trueCode
                        nid.name = wds[1]
                        nid.role = wds[2]
                        ids.add(nid)
                        idsMap.add(trueCode)
                        // System.out.printf("ID3: %s %s %s%n", nid.code, nid.role, nid.name);
                    } else {
                        java.lang.System.err.printf("Bad ID: %s%n", rls[k])
                    }
                }
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@media")) {
                val wds = ml(i)!!.split("[\\s:,]+".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (wds.size == 3) {
                    // try to find the mediafile according to type
                    mediaFilename = wds[1]
                    mediaType = wds[2]
                } else if (wds.size == 2) {
                    // type is not defined
                    mediaFilename = wds[1]
                    mediaType = "audio"
                }
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@id")) {
                val wds = ml(i)!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (wds.size < 3) {
                    java.lang.System.err.println("error on IDs for " + ml(i))
                    continue
                }
                var found = false
                var trueCode = wds[2]
                if (tparams.target.equals("dinlang")) {
                    // if in a list of special name, replace it
                    val oc = trueCode
                    // System.out.printf("IN @ID%n");
                    trueCode = equivalence(trueCode, tparams)
                    // System.out.printf("Changed (%s) --> (%s) %n", oc, trueCode);
                }
                for (id in ids) {
                    if (id.code == trueCode) {
                        found = true
                        // dans l'@ID
                        if (wds.size > 0) {
                            val details = wds[0].split(":\\s*".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                            //System.err.printf("ID: %s %d%n", wds[0], details.length);
                            if (details.size >= 2) {
                                //System.err.printf("ET: %s%n", details[0]);
                                //System.err.printf("LANG: %s%n", details[1]);
                                id.language = details[1]
                            }
                        }
                        if (wds.size > 1) id.corpus = wds[1].replace("\\s+".toRegex(), " ")
                        //code;
                        if (wds.size > 3) id.age = wds[3].replace("\\s+".toRegex(), " ")
                        if (wds.size > 4) id.sex = wds[4].replace("\\s+".toRegex(), " ")
                        if (wds.size > 5) id.group = wds[5].replace("\\s+".toRegex(), " ")
                        if (wds.size > 6) id.SES = wds[6].replace("\\s+".toRegex(), " ")
                        // role;
                        if (wds.size > 8) id.education = wds[8].replace("\\s+".toRegex(), " ")
                        if (wds.size > 9) id.customfield = wds[9].replace("\\s+".toRegex(), " ")
                    }
                }
                if (found == false) {
                    java.lang.System.err.println("error on ID " + ml(i) + "not found in participants - added to the list")
                    val nid: ID = ID()
                    // dans l'@ID
                    if (wds.size > 0) {
                        val details = wds[0].split(":\\s*".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (details.size >= 2) {
                            nid.language = details[1]
                        }
                    }
                    if (wds.size > 1) nid.corpus = wds[1]
                    if (wds.size > 2) nid.code = trueCode
                    if (wds.size > 3) nid.age = wds[3]
                    if (wds.size > 4) nid.sex = wds[4]
                    if (wds.size > 5) nid.group = wds[5]
                    if (wds.size > 6) nid.SES = wds[6]
                    if (wds.size > 7) nid.role = wds[7]
                    if (wds.size > 8) nid.education = wds[8]
                    if (wds.size > 9) nid.customfield = wds[9]
                    ids.add(nid)
                    idsMap.add(trueCode)
                }
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@location")) {
                location = ml(i)
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@date")) {
                date = ml(i)
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@birth")) {
                if (ml(i)!!.indexOf("of CHI") != -1) birth = ml(i)
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@comment")) {
                if (ml(i)!!.indexOf("coder") != -1 || ml(i)!!.indexOf("Coder") != -1) transcriber = ml(i) else {
                    comments.add(ml(i))
                }
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@transcriber")) {
                transcriber = ml(i)
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@situation") && inHeader) {
                situation = ml(i)
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@time Duration")) {
                timeDuration = ml(i)
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@g") || ml(i)!!.lowercase(Locale.getDefault())
                    .startsWith("@bg") || ml(i)!!.lowercase(Locale.getDefault()).startsWith("@eg") || ml(i)!!.lowercase(
                    Locale.getDefault()
                ).startsWith("@situation")
            ) {
                gemes.add(ml(i))
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@languages")) {
                var k = ml(i)!!.indexOf(':')
                if (k < 0) k = ml(i)!!.indexOf(' ')
                if (k < 0) k = ml(i)!!.indexOf('\t')
                if (k > 0) {
                    val tail = ml(i)!!.substring(k + 1).trim { it <= ' ' }
                    val w = tail.split("[\\s,]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (w.size > 0) lang = w
                }
            } else if (ml(i)!!.lowercase(Locale.getDefault()).startsWith("@time start")) {
                try {
                    timeStart = ml(i)!!.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                } catch (e: java.lang.Exception) {
                    if (!ml(i)!!.split("\t|\\s".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1].contains(":")) {
                        timeStart = ml(i)!!.split("\t|\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    }
                }
            } else if (ml(i)!!.startsWith("@") && ml(i)!!.split("\t".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray().size > 1 && inHeader) {
                otherInfo.add(ml(i))
            }
        }
        if (!verbose) return
        println("chat_filename : $chatFilename")
        println("media_filename : $mediaFilename")
        println("media_type : $mediaType")
        println("birth : $birth")
        println("date : $date")
        println("location : $location")
        println("situation : $situation")
        println("transcriber : $transcriber")
        println("language : $lang")
        for (com in comments) {
            println("com :  $com")
        }
        for (info in otherInfo) {
            println("info :  $info")
        }
        for (id in ids) {
            println("NAME : " + id.name)
            println("ID-language : " + id.language)
            println("ID-corpus : " + id.corpus)
            println("ID-code : " + id.code)
            println("ID-age : " + id.age)
            println("ID-sex : " + id.sex)
            println("ID-group : " + id.group)
            println("ID-SES : " + id.SES)
            println("ID-role : " + id.role)
            println("ID-education : " + id.education)
            println("ID-customfield : " + id.customfield)
        }
    }

    fun ageChild(): String? {
        return if (ids == null) "" else age("CHI")
    }

    fun age(part: String): String? {
        if (ids == null) return ""
        for (id in ids) if (id.code == part) return id.age
        return ""
    }

    fun ageJour(part: String): Int {
        if (ids == null) return -1
        for (id in ids) if (id.code == part) {
            var jours = 0
            var patternStr = "(\\d+);(\\d+).(\\d+)"
            var pattern: java.util.regex.Pattern = java.util.regex.Pattern.compile(patternStr)
            var matcher: java.util.regex.Matcher = pattern.matcher(id.age)
            if (matcher.find()) {
                jours = matcher.group(1).toInt() * 365
                jours += matcher.group(2).toInt() * 30
                jours += matcher.group(3).toInt()
                return jours
            }
            patternStr = "(\\d+);(\\d+)."
            pattern = java.util.regex.Pattern.compile(patternStr)
            matcher = pattern.matcher(id.age)
            if (matcher.find()) {
                jours = matcher.group(1).toInt() * 365
                jours += matcher.group(2).toInt() * 30
                return jours
            }
            patternStr = "(\\d+);(\\d+)"
            pattern = java.util.regex.Pattern.compile(patternStr)
            matcher = pattern.matcher(id.age)
            if (matcher.find()) {
                jours = matcher.group(1).toInt() * 365
                jours += matcher.group(2).toInt() * 30
                return jours
            }
            patternStr = "(\\d+);"
            pattern = java.util.regex.Pattern.compile(patternStr)
            matcher = pattern.matcher(id.age)
            if (matcher.find()) {
                jours = matcher.group(1).toInt() * 365
                return jours
            }
            patternStr = "(\\d+)"
            pattern = java.util.regex.Pattern.compile(patternStr)
            matcher = pattern.matcher(id.age)
            if (matcher.find()) {
                jours = matcher.group(1).toInt() * 365
                return jours
            }
        }
        return -1
    }

    fun corpus(part: String): String? {
        if (ids == null) return ""
        for (id in ids) if (id.code == part) return id.corpus
        return ""
    }

    fun name(part: String): String? {
        if (ids == null) return ""
        for (id in ids) if (id.code == part) return id.name
        return ""
    }

    fun role(part: String): String? {
        if (ids == null) return ""
        for (id in ids) if (id.code == part) return id.role
        return ""
    }

    fun code(c: Int): String {
        if (c < 0) return ""
        return if (c >= ids.size) "" else ids.get(c).role
        /*
           int i = 0;
           for (ID id: ids) {
               if ( i == c ) return id.role;
               i++;
           }
           return "";
           */
    }

    fun id(part: String): ID? {
        if (ids == null) return null
        for (id in ids) if (id.code == part) return id
        return null
    }

    internal inner class Tier {
        var tier: String
        var nl: Int

        constructor(t: String) {
            tier = t
            nl = -1
        }

        constructor(t: String, n: Int) {
            tier = t
            nl = n
        }
    }

    internal inner class MainTier {
        var mainLine: String? = null
        var mainCleaned: String
        var mainRaw: String
        var startTime = 0
        var endTime = 0
        var nl: Int
        var tiers: MutableList<Tier>?

        constructor(ml: String) {
            mainRaw = ml
            var patternStr = ".*\\x15(\\d+)_(\\d+)\\x15"
            var pattern: java.util.regex.Pattern = java.util.regex.Pattern.compile(patternStr)
            var matcher: java.util.regex.Matcher = pattern.matcher(ml)
            if (matcher.find()) {
                startTime = matcher.group(1).toInt()
                endTime = matcher.group(2).toInt()
                mainLine = ml.replace("\\x15\\d+_\\d+\\x15".toRegex(), "") // replaceFirst
                mainLine = mainLine!!.replace("\\t".toRegex(), " ")
                mainLine = mainLine!!.replace("\\p{C}".toRegex(), "")
            } else {
                patternStr = ".*\\x15(.*?)_(\\d+)_(\\d+)\\x15"
                pattern = java.util.regex.Pattern.compile(patternStr)
                matcher = pattern.matcher(ml)
                if (matcher.find()) {
                    /*
					if (!matcher.group(1).isEmpty()) {
						System.err.println("pat found: " + matcher.group(1));
					}
					*/
                    startTime = matcher.group(2).toInt()
                    endTime = matcher.group(3).toInt()
                    mainLine = ml.replace("\\x15.*?\\x15".toRegex(), "") // replaceFirst
                    mainLine = mainLine!!.replace("\\t".toRegex(), " ")
                    mainLine = mainLine!!.replace("\\p{C}".toRegex(), "")
                } else {
                    startTime = -1
                    endTime = -1
                    mainLine = ml
                    mainLine = mainLine!!.replace("\\t".toRegex(), " ")
                    mainLine = mainLine!!.replace("\\p{C}".toRegex(), "")
                }
            }
            mainCleaned = ConventionsToChat.clean(mainLine)
            nl = -1
            tiers = null
        }

        constructor(ml: String, start: Int, end: Int) {
            mainRaw = ml
            startTime = start
            endTime = end
            mainLine = ml
            mainCleaned = ConventionsToChat.clean(ml)
            nl = -1
            tiers = null
        }

        constructor(ml: String, style: String) {
            var ml = ml
            ml = if (style == "noparticipant") {
                "*LOC: $ml"
            } else {
                "*" + ml.trim { it <= ' ' }
            }
            mainRaw = ml
            mainLine = ml
            mainCleaned = ConventionsToChat.clean(mainLine)
            nl = -1
            tiers = null
        }

        constructor(ml: String, n: Int) : this(ml) {
            nl = n
        }

        fun addTier(tier: String) {
            if (tiers == null) tiers = LinkedList<Tier>()
            tiers!!.add(Tier(tier.replace("\\x15\\d+_\\d+\\x15".toRegex(), ""))) //tier
        }

        fun addTier(tier: String?, n: Int) {
            if (tiers == null) tiers = LinkedList<Tier>()
            tiers!!.add(Tier(tier, n))
        }

        fun ml(): String? {
            return mainLine
        }

        fun mlc(): String {
            return mainCleaned
        }

        fun start(): Int {
            return startTime
        }

        fun end(): Int {
            return endTime
        }

        fun t(n: Int): String {
            return tiers!![n].tier
        }

        fun mlLNB(): Int {
            return nl
        }

        fun tLNB(n: Int): Int {
            return tiers!![n].nl
        }

        fun majtime(ts: Int, te: Int) {
            startTime = ts
            endTime = te
        }
    }

    var mainLines: MutableList<MainTier>

    init {
        /**
         * initialise une donnée de type ChatFile
         */
        mainLines = java.util.ArrayList<MainTier>()
    }

    fun addML(ml: String) {
        /*
		 * split into parts if the are more than one "\\x15.*?\\x15"
		 */
        val pattern: java.util.regex.Pattern = java.util.regex.Pattern.compile("\\x15.*?\\x15")
        val matcher: java.util.regex.Matcher = pattern.matcher(ml)

        // look for the first pattern
        if (!matcher.find()) {
            // no pattern at all
            // process whole line directly
//			System.out.println("DIRECT:" + ml);
            mainLines.add(MainTier(ml))
            return
        }
        // store the pattern
        var start = 0 // beginning of line
        var end: Int = matcher.end() // end of pattern
        val cl = ChatLine(ml)

//		System.out.println("SE1: " + start + " " + end + "||||" + ml);
        while (matcher.find()) {
            // found the pattern "+matcher.group()+" starting at index "+
            // matcher.start()+" and ending at index "+matcher.end());
            // find another pattern
            // process the previous one
            if (start == 0) {
                mainLines.add(MainTier(ml.substring(start, end)))
                //        		System.out.println("SENEXT: " + start + " " + end + "||||" + ml.substring(start, end));
            } else {
                mainLines.add(MainTier(cl.type() + ":\t" + ml.substring(start, end)))
                //        		System.out.println("SENEXT(U): " + start + " " + end + "||||" + cl.type() + ":\t" + ml.substring(start, end));
            }
            start = end + 1 // follows previous pattern
            end = matcher.end() // end of current pattern
        }

        // final part up to end of line
        if (start == 0) {
//    		System.out.println("SEFINAL: " + start + " " + end + "||||" + ml.substring(start));
            mainLines.add(MainTier(ml))
        } else {
            mainLines.add(MainTier(cl.type() + ":\t" + ml.substring(start, end)))
            //    		System.out.println("SENFINAL(U): " + start + " " + end + "||||" + cl.type() + ":\t" + ml.substring(start, end));
        }
    }

    /*	void addML(String ml, int n) {
		mainLines.add( new MainTier(ml, n) );
	}
*/
    fun addT(ml: String) {
        val last = mainLines[mainLines.size - 1]
        last.addTier(ml)
    }

    /*	void addT(String ml, int n) {
		MainTier last = mainLines.get( mainLines.size()-1 );
		last.addTier( ml, n );
	}
*/
    fun equivalence(part: String, tparams: TierParams): String {
//		System.out.printf("EQUIVALENCE FOR [%s]%n", part);
        for ((key, value): Map.Entry<String, SpkVal> in tparams.tv.entrySet()) {
            if (key == part) {
                // System.out.printf("FOUND>> [%s] [%s] [%s]%n", part, key, entry.getValue().genericvalue);
                return value.genericvalue
            }
        }
        // System.out.printf("NOT FOUND>> [%s]%n", part);
        return part
    }

    fun insertML(ml: String, tparams: TierParams) {
        if (ml.startsWith("%")) {
            if (inMainLine == false) {
                inMainLine = true
                addML("*UNK:\t.") // adds an empty line
            }
            addT(ml)
        } else {
            if (ml.startsWith("*")) {
                inMainLine = true
                //				System.out.printf("Dinlang (%s)%n", tparams.target);
                if (tparams.target.equals("dinlang")) {
                    // extract name of participant
//					System.out.printf("Dinlang passed%n");
                    val cl = ChatLine(ml)
                    // if in a list of special name, replace it
                    val code: String = cl.head.substring(1)
                    //					System.out.printf("IN insertML%n");
                    val dinlangName = equivalence(code, tparams)
                    // System.out.printf("Changed (%s) --> (%s) %n", code, dinlangName);
                    // split the line in a main line plus a secondary line
                    // get time is there is one
                    val patternStr = "(.*)(\\x15\\d+_\\d+\\x15)(.*)"
                    val pattern: java.util.regex.Pattern = java.util.regex.Pattern.compile(patternStr)
                    val matcher: java.util.regex.Matcher = pattern.matcher(cl.tail)
                    if (matcher.find()) {
                        // add the main line with the time in it
                        addML("*" + dinlangName + ":\t0 " + matcher.group(2))
                        // add the secondary line
                        addT("%" + Utils.languagingScript + ":\t" + matcher.group(1) + " " + matcher.group(3))
                    } else {
                        // add the main line (without time)
                        addML("*$dinlangName:\t0")
                        // add the secondary line
                        addT("%" + Utils.languagingScript + ":\t" + cl.tail)
                    }
                } else {
                    addML(ml)
                }
            } else {
                inMainLine = false
                addML(ml)
            }
        }
    }

    @Throws(IOException::class)
    fun load(fn: String, tparams: TierParams) {
        if (tparams.inputFormat.equals(".txt")) {
            loadText(fn, tparams)
            return
        }
        if (tparams.inputFormat.equals(".srt")) {
            loadSrt(fn, tparams)
            return
        }
        chatFilename = fn
        var line = ""
        var ml = ""
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(java.io.InputStreamReader(FileInputStream(fn), inputEncoding))
            while (reader.readLine().also { line = it } != null) {
                // Traitement du flux de sortie de l'application si besoin est
                if (line.startsWith(" ")) {
                    ml += line
                } else if (line.startsWith("\t")) {
                    ml += " " + line.substring(1)
                } else {
                    // process previous line if not empty
                    if (ml != "") {
                        insertML(ml, tparams)
                    }
                    ml = line
                }
            }
        } catch (fnfe: FileNotFoundException) {
            java.lang.System.err.println("Erreur fichier : $fn indisponible")
            java.lang.System.exit(1)
            return
        } catch (ioe: IOException) {
            java.lang.System.err.println("Erreur sur fichier : $fn")
            ioe.printStackTrace()
            java.lang.System.exit(1)
        } finally {
            if (ml != "") insertML(ml, tparams)
            if (reader != null) reader.close()
        }
    }

    @Throws(IOException::class)
    fun loadText(fn: String, tparams: TierParams) {
        chatFilename = fn
        var line: String? = ""
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(java.io.InputStreamReader(FileInputStream(fn), inputEncoding))
            while (reader.readLine().also { line = it } != null) {
                mainLines.add(MainTier(line, tparams.normalize))
            }
        } catch (fnfe: FileNotFoundException) {
            java.lang.System.err.println("Erreur fichier : $fn indisponible")
            java.lang.System.exit(1)
            return
        } catch (ioe: IOException) {
            java.lang.System.err.println("Erreur sur fichier : $fn")
            ioe.printStackTrace()
            java.lang.System.exit(1)
        } finally {
            if (reader != null) reader.close()
        }
    }

    @Throws(IOException::class)
    fun loadSrt(fn: String, tparams: TierParams) {
        chatFilename = fn
        var line = ""
        val ml = ""
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(java.io.InputStreamReader(FileInputStream(fn), inputEncoding))
            while (reader.readLine().also { line = it } != null) {
                if (line.isEmpty()) continue
                // read an srt triplet
                // first number
                var nb: Int
                var start = -1
                var end = -1
                try {
                    nb = line.toInt()
                } catch (e: java.lang.Exception) {
                    nb = 0
                    java.lang.System.out.printf("Error srt: bad number at %s%n", line)
                }
                if (reader.readLine().also { line = it } == null) {
                    java.lang.System.out.printf("Error srt: incomplete last element%n", nb)
                    break
                }
                //System.out.printf("srt: %d%n", nb);
                var regex = "([\\d:,]+)\\s+-{2}\\>\\s+([\\d:,]+)"
                var p: java.util.regex.Pattern = java.util.regex.Pattern.compile(regex)
                var m: java.util.regex.Matcher = p.matcher(line)
                if (m.find()) {
                    val sbegin: String = m.group(1)
                    val send: String = m.group(2)
                    // System.out.printf("found: %s %s%n", m.group(1), m.group(2));
                    // START
                    regex = "(\\d+):(\\d+):(\\d+),(\\d+)"
                    p = java.util.regex.Pattern.compile(regex)
                    m = p.matcher(sbegin)
                    if (m.find()) {
                        val h: Int = m.group(1).toInt()
                        val mn: Int = m.group(2).toInt()
                        val s: Int = m.group(3).toInt()
                        val ms: Int = m.group(4).toInt()
                        start = (h * 3600 + mn * 60 + s) * 1000 + ms
                    } else {
                        regex = "(\\d+):(\\d+):(\\d+)"
                        p = java.util.regex.Pattern.compile(regex)
                        m = p.matcher(sbegin)
                        if (m.find()) {
                            val h: Int = m.group(1).toInt()
                            val mn: Int = m.group(2).toInt()
                            val s: Int = m.group(3).toInt()
                            start = (h * 3600 + mn * 60 + s) * 1000
                        }
                    }
                    // END
                    regex = "(\\d+):(\\d+):(\\d+),(\\d+)"
                    p = java.util.regex.Pattern.compile(regex)
                    m = p.matcher(send)
                    if (m.find()) {
                        val h: Int = m.group(1).toInt()
                        val mn: Int = m.group(2).toInt()
                        val s: Int = m.group(3).toInt()
                        val ms: Int = m.group(4).toInt()
                        end = (h * 3600 + mn * 60 + s) * 1000 + ms
                    } else {
                        regex = "(\\d+):(\\d+):(\\d+)"
                        p = java.util.regex.Pattern.compile(regex)
                        m = p.matcher(send)
                        if (m.find()) {
                            val h: Int = m.group(1).toInt()
                            val mn: Int = m.group(2).toInt()
                            val s: Int = m.group(3).toInt()
                            end = (h * 3600 + mn * 60 + s) * 1000
                        }
                    }
                } else {
                    java.lang.System.out.printf("srt: cannot process: %s%n", line)
                }
                var content = "*SRT"
                while (reader.readLine().also { line = it } != null) {
                    if (line.isEmpty()) break
                    content += " " + line.trim { it <= ' ' }
                }
                // System.out.printf("content: %d %d %d %s%n", nb, start, end, content);
                mainLines.add(MainTier(content, start, end))
            }
        } catch (fnfe: FileNotFoundException) {
            java.lang.System.err.println("Erreur fichier : $fn indisponible")
            java.lang.System.exit(1)
            return
        } catch (ioe: IOException) {
            java.lang.System.err.println("Erreur sur fichier : $fn")
            ioe.printStackTrace()
            java.lang.System.exit(1)
        } finally {
            if (ml != "") insertML(ml, tparams)
            if (reader != null) reader.close()
        }
    }

    fun nbMainLines(): Int {
        return mainLines.size
    }

    fun ml(n: Int): String? {
        return mainLines[n].ml()
    }

    fun mlc(n: Int): String {
        return mainLines[n].mlc()
    }

    fun startMl(n: Int): Int {
        return mainLines[n].start()
    }

    fun endMl(n: Int): Int {
        return mainLines[n].end()
    }

    fun majtime(n: Int, ts: Int, te: Int) {
        mainLines[n].majtime(ts, te)
    }

    fun nbTiers(n: Int): Int {
        return if (mainLines[n].tiers == null) 0 else mainLines[n].tiers!!.size
    }

    fun t(n: Int, t: Int): String {
        return mainLines[n].t(t)
    }

    fun filename(): String? {
        return chatFilename
    }

    fun dumpHeader() {
        println("Filename : " + filename())
        println("Nb Lines : " + nbMainLines())
        val nbids: Int = ids.size
        println("Nb IDs : $nbids")
        println("chat_filename : $chatFilename")
        println("media_filename : $mediaFilename")
        println("media_type : $mediaType")
        println("birth : $birth")
        println("date : $date")
        println("location : $location")
        println("situation : $situation")
        println("transcriber : $transcriber")
        println("language : $lang")
        for (com in comments) {
            println("com :  $com")
        }
        for (info in otherInfo) {
            println("info :  $info")
        }
        for (id in ids) {
            println("NAME : " + id.name)
            println("ID-language : " + id.language)
            println("ID-corpus : " + id.corpus)
            println("ID-code : " + id.code)
            println("ID-age : " + id.age)
            println("ID-sex : " + id.sex)
            println("ID-group : " + id.group)
            println("ID-SES : " + id.SES)
            println("ID-role : " + id.role)
            println("ID-education : " + id.education)
            println("ID-customfield : " + id.customfield)
        }
    }

    fun dump() {
        dumpHeader()
        val sz = nbMainLines()
        for (i in 0 until sz) {
            println(i.toString() + ": (" + startMl(i) + ") (" + endMl(i) + ") " + ml(i))
            println(i.toString() + ": (" + startMl(i) + ") (" + endMl(i) + ") " + mlc(i))
            val tsz = nbTiers(i)
            for (j in 0 until tsz) {
                println(j.toString() + "- " + t(i, j))
            }
        }
    }

    fun cleantime_inmemory(style: Int) {
        var last_te = -1
        var last_ts = -1
        var last_i = -1
        var missing = 1
        var i: Int
        var first = 0
        i = 0
        while (i < nbMainLines()) {
            val tp = ml(i)
            if (tp!!.startsWith("*")) {
                if (missing == 1) {
                    first = i
                    missing = 0
                }
                if (endMl(i) > 0) {
                    break
                }
            }
            i++
        }
        // i tells where there is the first bullet and first tells where there is the first line where there should be a bullet.
        if (i == nbMainLines()) {
            // no bullet at all - note that it could be possible to spread the time across ALL the transcription
            println("Warning: no bullet in file - impossible to cleantime")
            return
        }

        // if the first bullet does not correspond to the first line, then we move that bullet to the first line.
        if (i != first) {
            val ts = startMl(i)
            val te = endMl(i)
            majtime(i, -1, -1)
            majtime(first, ts, te)
        }
        missing = 0 // count how many missing lines between two bullets.
        i = 0
        while (i < nbMainLines()) {
            val tp = ml(i)
            if (tp!!.startsWith("*")) {
                val ts = startMl(i)
                val te = endMl(i)
                //				System.out.println("found bullet at " + i + " " + ts + " " + te + " missing " + missing);
                if (ts != -1) { // this means that there is a legal bullet on that line
                    if (missing > 0) { // it is necessary to propagate
                        // propagate time after bullet
                        if (style == 0) { // equal length between all lines
                            val d = (last_te - last_ts) / (missing + 1)
                            var k = 0
                            while (k < missing) {
                                val new_ts = last_ts + k * d
                                val new_te = last_ts + k * d + d
                                // upgrade last_i with new_ts and new_te
                                majtime(last_i, new_ts, new_te)
                                // find new line to be bulleted
                                var tstop: String?
                                do {
                                    last_i++
                                    tstop = ml(last_i)
                                } while (!tstop!!.startsWith("*"))
                                k++
                            }
                            // propagate to last
                            val new_ts = last_ts + k * d
                            val new_te = last_te
                            // upgrade last_i with new_ts and new_te
                            majtime(last_i, new_ts, new_te)
                        } else {
                            // compute the length in words of the line to be bulleted
                            var k: Int
                            var li = last_i
                            var total_nw = 0
                            val nw = IntArray(missing + 1)
                            k = 0
                            while (k <= missing) {

                                // count number of words
                                val w = mlc(li).split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                                nw[k] = w.size
                                total_nw += nw[k]
                                // find new line to be bulleted
                                var tstop: String?
                                do {
                                    li++
                                    tstop = ml(li)
                                } while (!tstop!!.startsWith("*"))
                                k++
                            }
                            // compute the time increment for each word
                            val d = (last_te - last_ts) / (total_nw + 1)
                            var decal = 0 // compute current new bullet time
                            // bullet the lines according to their number of words
                            k = 0
                            while (k < missing) {

                                // compute length in milliseconds
                                val lg = nw[k] * d
                                val new_ts = last_ts + decal
                                val new_te = last_ts + decal + lg
                                decal += lg
                                // upgrade last_i with new_ts and new_te
                                majtime(last_i, new_ts, new_te)
                                // find new line to be bulleted
                                var tstop: String?
                                do {
                                    last_i++
                                    tstop = ml(last_i)
                                } while (!tstop!!.startsWith("*"))
                                k++
                            }
                            // propagate to last
                            val new_ts = last_ts + decal
                            val new_te = last_te
                            // upgrade last_i with new_ts and new_te
                            majtime(last_i, new_ts, new_te)
                        }
                    }
                    // store last line with a bullet
                    last_ts = ts
                    last_te = te
                    last_i = i
                    missing = 0
                } else {
                    // count how many lines have no bullet
                    missing++
                }
            }
            i++
        }
        if (missing > 0) {
            // propagate time after the last bullet of the file
            val d = (last_te - last_ts) / (missing + 1) // equal time between all new bullets.
            var k = 0
            while (k < missing) {
                val new_ts = last_ts + k * d
                val new_te = last_ts + k * d + d
                // upgrade last_i with new_ts and new_te
                majtime(last_i, new_ts, new_te)
                last_i++
                k++
            }
            // propagate to very last line
            val new_ts = last_ts + k * d
            val new_te = last_te
            // upgrade last_i with new_ts and new_te
            majtime(last_i, new_ts, new_te)
        }
    }

    @Throws(java.lang.Exception::class)
    fun init(fn: String) {
        /**
         * lit le contenu d'un fichier.
         * lit et décompose les entetes
         * exception en cas de fichier absent ou incorrect.
         * @param fn fichier Chat à lire
         */
        val tp = TierParams()
        load(fn, tp)
        findInfo(false, tp)
        cleantime_inmemory(1)
    }

    companion object {
        /** All input will use this encoding  */
        const val inputEncoding = "UTF-8"
        fun loc(ln: String): String {
            val n = ln.indexOf(':')
            return if (n == -1) ln else ln.substring(1, n)
        }

        fun ctn(ln: String): String {
            val n = ln.indexOf(':')
            return if (n == -1) ln else ln.substring(n + 1, ln.length).trim { it <= ' ' }
        }

        @Throws(java.lang.Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val tp = TierParams()
            val cf = ChatFile()
            cf.load(args[0], tp)
            cf.findInfo(false, tp)
            cf.dump()
        }
    }
}
