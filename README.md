# json-diff
## Installation 
Using [ktm](https://github.com/ghostbuster91/ktm)

`$ ktm install com.github.ghostbuster91:json-diff --version 0.1`

### Example:

`$ json-diff sample/big_list.json sample/big_list_2.json -m ".items[]" "id"`

Result:
```
Found 6 differences

Diff number 1:
Affected key: .items[].created_at
First value: 1533993499163
Second value: 1.533993499163E12
Where first belongs to 
{
  "about": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d",
  "author": "0x0548e5edb8efa588339f8398aeb349804e5f693e",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:307583",
  "created_at": "1533993499163",
  "family": "http",
  "id": "claim:0x5b00c6834f15a3ca908856cd25bef9bb1b7a06def790a6c3056371f1e83ffc5c1c80610cc9928e58556c7b58b41fb4fb5c3671502990184251ee1d6391cac3021c",
  "likes": [],
  "replies": [],
  "sequence": 3401.0,
  "target": "最近辛いもの食べすぎてお腹下しそうにょ....",
  "type": "post_club"
}
And second belongs to 
{
  "about": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d",
  "author": "0x0548e5edb8efa588339f8398aeb349804e5f693e",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:307583",
  "created_at": 1.533993499163E12,
  "family": "http",
  "id": "claim:0x5b00c6834f15a3ca908856cd25bef9bb1b7a06def790a6c3056371f1e83ffc5c1c80610cc9928e58556c7b58b41fb4fb5c3671502990184251ee1d6391cac3021c",
  "likes": [],
  "replies": [],
  "sequence": 3401.0,
  "target": "最近辛いもの食べすぎてお腹下しそうにょ....",
  "type": "post_club"
}
====================================================================================


Diff number 2:
Affected key: .items[].replies[].family
First value: http
Second value: something something
Where first belongs to 
{
  "author": "0xdb6047646ea3928b79d92844aa164d1106b69b27",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:839415",
  "created_at": 1.533983700447E12,
  "family": "http",
  "id": "claim:0x62bc67cb81beaa089f83f80a039c938abd95e3619ae325073cd9a0d1b8e6d97645233035adab566868d7817d261499085cb4d076515ad738667ef2ae935d6ad61b",
  "likes": [],
  "sequence": 3392.0,
  "target": "飼い主によく似てるにゃ"
}
And second belongs to 
{
  "author": "0xdb6047646ea3928b79d92844aa164d1106b69b27",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:839415",
  "created_at": 1.533983700447E12,
  "family": "something something",
  "id": "claim:0x62bc67cb81beaa089f83f80a039c938abd95e3619ae325073cd9a0d1b8e6d97645233035adab566868d7817d261499085cb4d076515ad738667ef2ae935d6ad61b",
  "likes": [],
  "sequence": 3392.0,
  "target": "飼い主によく似てるにゃ"
}
====================================================================================


Diff number 3:
Affected key: .items[].replies[].created_at
First value: 1.533994530387E12
Second value: other
Where first belongs to 
{
  "author": "0xdb6047646ea3928b79d92844aa164d1106b69b27",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:839415",
  "created_at": 1.533994530387E12,
  "family": "http",
  "id": "claim:0x9e1b32c86a33eb76061555c71de0018df30aa5aa4e95fa4775eb1b7ebe136a360c7d5eb7c0c6bdfb03d1c948d50d861ab334376d94eb9c534274b07caed450a01b",
  "likes": [],
  "sequence": 3415.0,
  "target": "かそーつーかってなに？"
}
And second belongs to 
{
  "author": "0xdb6047646ea3928b79d92844aa164d1106b69b27",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:839415",
  "created_at": "other",
  "family": "http",
  "id": "claim:0x9e1b32c86a33eb76061555c71de0018df30aa5aa4e95fa4775eb1b7ebe136a360c7d5eb7c0c6bdfb03d1c948d50d861ab334376d94eb9c534274b07caed450a01b",
  "likes": [],
  "sequence": 3415.0,
  "target": "かそーつーかってなに？"
}
====================================================================================


Diff number 4:
Affected key: .items[]
First value: {
  "author": "0x0537544de3935408246ee2ad09949d046f92574d",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:860290",
  "created_at": 1.533960774187E12,
  "family": "http",
  "id": "claim:0xabdbfb05249079c90e26fe181cafce8f907c1a46c14c6ff4f810994529cf3df75604ec860fb31d78205f73bbc61a5afcae3189f576da418b12244f5fba413eef1c",
  "likes": [],
  "replies": [],
  "sequence": 3378.0,
  "target": "ぼく、煽り皮肉ネコだけど\nなんかモンクある？\nん？",
  "type": "regular"
}
Second value: null
Where first belongs to 
{
  "items": []
}
And second belongs to 
{
  "items": []
}
====================================================================================


Diff number 5:
Affected key: .items[]
First value: null
Second value: {
  "about": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d",
  "author": "0x0548e5edb8efa588339f8398aeb349804e5f693e",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:307583",
  "created_at": 1.53395595301E12,
  "family": "http",
  "id": "claim:0x25dc6482272f5f532c3135f0632150decc2b599df89fc82bf38ffeb658bf7c5d5aed02e0f56baa15297c6bccdde4d503b0ed11ccc9b172a1c94b22a0ae4dd0f21c",
  "likes": [],
  "replies": [],
  "sequence": 3374.0,
  "target": "おはにょ、先日のにゃんこミートアップ行きたかったにょ...",
  "type": "post_club"
}
Where first belongs to 
{
  "items": []
}
And second belongs to 
{
  "items": []
}
====================================================================================


Diff number 6:
Affected key: .items[]
First value: null
Second value: {
  "author": "0xdb6047646ea3928b79d92844aa164d1106b69b27",
  "context": "ethereum:0x06012c8cf97bead5deae237070f9587f8e7a266d:839415",
  "created_at": 1.533947186217E12,
  "family": "http",
  "id": "claim:0xa22f8cb55764080c4a04ce9c0d1e9a16a21973bc422640a53b5729c9754aaecb5faefc1a665f6195c34d83dceb8afc6f8449bcfd074697c8bb86d461cddfd7811b",
  "likes": [],
  "replies": [],
  "sequence": 3370.0,
  "target": "みゃおー！\n今日からお盆にゃ\nみんにゃはどうやって過ごすのにゃ？",
  "type": "regular"
}
Where first belongs to 
{
  "items": []
}
And second belongs to 
{
  "items": []
}
====================================================================================
```
