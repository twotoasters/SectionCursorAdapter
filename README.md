# SectionCursorAdapter

![sections](screenshots/sections.png) ![dialog](screenshots/dialog.png)

SectionCursorAdapter adds sections and fast scroll to CursorAdapter as an easily implementable feature. 

This is implemented in a similar way to Android's CursorAdapter which this extends. Instead of having `newView` and `bindView` the SectionCursorAdpater uses `newSectionView` and `bindSectionView` plus `newItemView` and `bindItemView`. 

There is one additional abstract method `SortedMap<Integer, Object> buildSections(Cursor cursor)`. This is the method which tells the adapter how to remap the cursor positions to allow for sections. You can make an alphabitical adapter with the following method. Noob tip: You will have to sort alphabitically when querying your database for your cursor.

    @Override
    protected SortedMap<Integer, Object> buildSections(Cursor cursor) {
        TreeMap<Integer, Object> sections = new TreeMap<>();
        int nameColumnIndex = cursor.getColumnIndex(StoreModel.NAME);
        int cursorPosition = 0;
        
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameColumnIndex);
            String section = name.toUpperCase().substring(0, 1);
            if (!sections.containsValue(section)) {
                sections.put(cursorPosition + sections.size(), section);
            }
            cursorPosition++;
        }
        return sections;
    }

For more advanced sections you can give a custom object as a value in the map instead of a number or string. This object is then passed through to newSectionView and bindSectionView. To use the fast scroll override `toString` for this object to control what you want displayed in the fast scroll dialog. Note that in versions of Android before KitKat this dialog does not resize to fit content. SectionCursorAdapter only allows a maximum of 3 characters in this dialog on these older version of Android but by overriding `getMaxIndexerLength()` the length will can to whatever you choose.

## License

    Copyright 2014 Two Toasters
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
       
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
