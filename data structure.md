# Inner data structure of a recording

Data structure to store the content of a transcription/coding of a media (single or multiple) that can be audio and/or video (or anything else)

## Metadata

Metadata contains all information that is not in the transcription/coding

Dublin core information extended and more precise

### Document information

  - Author, ...
  - Rights, ...

### Transcription information

  - Media information
  - Setting information
  - Participants information 

## Text

The text can be presented in hierarchical format or partition format. In some partition formats, there are some hierarchical relations between different partitions lines. There are several problems when going from a format to another. One is that there are ordering that are absolute because they depend on time, and there are ordering which are relative because they depend on the position in an order (using ordinal numbers). Another is that the name/class of an item can be specific or generic. When it is generic, if an item is dependent from another item of the hierarchy, this can change the status from generic to specific. Also, the items can have a name and a type. The role of the type is normally to decribed the nature of the content of the item, but in some case, the name of the type is also used to name the item.

We use a generic form for items in a text (of any type). The items will contain all possible information, when it exists: time (start, end and/or duration), position (at their level), relation to a parent, name, type. A list of dependent items can be added for the purpose of speed of processing, but this list can be recomputed at any time.

Types can point to formats, and formats include vocabularies.

Index tables can be computed for the main level, or for dependent levels (then they will included in a item - see list of dependent items).

### Template: Information about name(s) and type(s)

#### Names
Names represent the speaker/content/form/etc. information. The name of a tier in many software: a string.

#### Types
Types represent the structure and properties of a tier.

##### Format of types
  - 'parent-type': name of the type of the parent tier
  - 'time-structure': temporal organisation of the type: none, unrestricted, timediv, included
  - structure: symbolic organisation of the type: none, subdivision, association
  - content: organisation of the transcription part: any (anything), CHAT (chat format), TRANSCRIBER (Transcriber format), CV (vocabularies)

### Formats of items in a text

  - obligatory: id, name, type, parent, 'time-start', 'time-end', index, content
  - optional: list of first descendants, predecessor, follower, pointer to index table
  
Notes:
  - all id are unique and cannot ever change because they are used for parent child relationship
  - either the 'time-start', 'time-end' are provided, or the index. Both cannot be undefined.
  - time information is provided in seconds with floating point value with any number of digits as needed
  - parent is a pointer to an element (relation between types are represented in types)
  - all optional elements can be reconstructed at any time

All the examples are presented in JSON format

### Reading a clan file

Main lines (starting with '*')
```
:-ex: *CHI: I want a biscuit. %1001_1234%
{ id: 'x4810', name: 'CHI', type: 'chat-mainline', parent: '', 'time-start': 1001, 'time-end': 1234, index: 28, content: 'I want a biscuit.' }
```

Secondary lines (starting with '%')
```
:-ex: %act: CHI point to the biscuit.
{ id: 'x4811', name: 'act', type: 'chat-depline', parent: 'x4810', 'time-start': -1, 'time-end': -1, index: 1, content: 'CHI point to the biscuit.' }
```

Corresponding types:
```
'chat-mainline' = { 'parent-type': '', 'time-structure': unrestricted, structure: none, content: 'CHAT' }
'chat-depline' = { 'parent-type': 'chat-mainline', 'time-structure': none, structure: association, content: any }
```

### Reading an elan file

All lines have the same format: a tier name, a type name, some characteristics of the type go into the representation, time information (time structures) or a pointer to a parent and a position in relation with the parent (symbolic structures)

ELAN tiers with no type (default-lt)
```
:-ex: OBS | I come |   | but not now. |         | oh yes ! |   (vertical bars represent the limits of an annotation in ELAN)
{ id: 'x2209', name: 'OBS', type: "ELAN-default-lt", parent: '', 'time-start': 1001, 'time-end': 1890, index: 3, content: 'I come' }
{ id: 'x2217', name: 'OBS', type: "ELAN-default-lt", parent: '', 'time-start': 2305, 'time-end': 2807, index: 4, content: 'but not now.' }
{ id: 'x2367', name: 'OBS', type: "ELAN-default-lt", parent: '', 'time-start': 7266, 'time-end': 7845, index: 5, content: 'oh yes.' }
```

ELAN tiers with type (timediv relation)
```
:-ex: OBS-gest | raise hand | pointing |     (vertical bars represent the limits of an annotation in ELAN)
{ id: 'x3134', name: 'OBS-gest', type: "gesture", parent: 'x2209', 'time-start': 91128, 'time-end': 91233, index: 1, content: 'raise hand' }
{ id: 'x3135', name: 'OBS-gest', type: "gesture", parent: 'x2209', 'time-start': 91233, 'time-end': 92567, index: 2, content: 'pointing' }
```

ELAN tiers with type (symbolic subdivision relation)
```
:-ex: OBS-pos | PRON-SUBJ | VERB  | PRON-OBJ |  (vertical bars represent the limits of an annotation in ELAN)
{ id: 'x37', name: 'OBS-pos', type: "partofspeech", parent: 'x2209', 'time-start': -1, 'time-end': -1, index: 1, content: 'I come' }
{ id: 'x38', name: 'OBS-pos', type: "partofspeech", parent: 'x2209', 'time-start': -1, 'time-end': -1, index: 2, content: 'but not now.' }
{ id: 'x39', name: 'OBS-pos', type: "partofspeech", parent: 'x2209', 'time-start': -1, 'time-end': -1, index: 3, content: 'oh yes.' }
```

Corresponding types:
```
'ELAN-default-lt' = { 'parent-type': '', 'time-structure': unrestricted, content: any }
'gesture' = { 'parent-type': 'ELAN-default-lt', 'time-structure': timediv, structure: none, content: any }
'partofspeech' = { 'parent-type': 'ELAN-default-lt', 'time-structure': none, structure: subdivision, content: 'POS' }
```

### Reading a Praat file

All lines have the same format: a tier name, a type name (point or not point), time information
```
:-ex: OBS | I come |   | but not now. |         | oh yes ! |   (vertical bars represent the time limits of annotations in Praat - no holes)
{ id: 'x2209', name: 'OBS', type: '', parent: '', 'time-start': 1001, 'time-end': 1890, index: 3, content: 'I come' }
{ id: 'x2211', name: 'OBS', type: '', parent: '', 'time-start': 1890, 'time-end': 2305, index: 3, content: '' }
{ id: 'x2217', name: 'OBS', type: '', parent: '', 'time-start': 2305, 'time-end': 2807, index: 4, content: 'but not now.' }
{ id: 'x2223', name: 'OBS', type: '', parent: '', 'time-start': 2807, 'time-end': 7266, index: 4, content: '' }
{ id: 'x2367', name: 'OBS', type: '', parent: '', 'time-start': 7266, 'time-end': 7845, index: 5, content: 'oh yes.' }
```

### Reading a Transcriber file

Main lines are with one speaker, or with several speakers
```
:-ex: spk1: anchor:1001  spk1:I want a biscuit. anchor:1234
{ id: 'x4810', name: 'spk1', type: 'Transcriber-main', parent: '', 'time-start': 1001, 'time-end': 1234, index: 28, content: 'I want a biscuit.' }
:-ex: spk1,spk2: anchor:1001  spk1:I want a biscuit. spk2: what? anchor:1234
{ id: 'x4810', name: 'spk1', type: 'Transcriber-main', parent: '', 'time-start': 1001, 'time-end': 1234, index: 28, content: 'I want a biscuit.' }
{ id: 'x4811', name: 'spk2', type: 'Transcriber-main', parent: '', 'time-start': 1001, 'time-end': 1234, index: 29, content: 'what?' }
```

Backchannels lines can exist

Corresponding types:
```
'Transcriber-main' = { 'parent-type': '', 'time-structure': unrestricted, structure: none, content: 'TRANSCRIBER' }
```
