## Sample JSON:
Name of JSON file is the item ID of the item you want to add/remove/modify attribute modifiers to.
```json5
{
    "mainhand": {                                               // name of the equipment slot the modifiers apply to
        "removed": [                                            //
            "cb3f55d3-645c-4f38-a497-9c13a33db5cf"              // UUID of modifier to be removed; in this case, it's the attack damage UUID
        ],                                                      //
        "modifiers": [                                          //
            {                                                   //
                "attribute": "generic_attack_damage",           //
                "uuid": "fa233e1c-4180-4865-b01b-bcce9785aca3", // UUID of modifier to be added; will replace if already exists
                "name": "Weapon Modifier",                      // optional; will default to UUID if not supplied
                "operation": "add",                             // one of add, multiply_base, multiply_total
                "value": -2.0                                   // value of the attribute modifier
            }
        ]
    },
    "head": {
        // ...
    }
}
```