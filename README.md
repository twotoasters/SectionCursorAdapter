# SectionCursorAdapter [![Run Status](https://api.shippable.com/projects/572b5e982a8192902e1f1467/badge?branch=master)](https://app.shippable.com/projects/572b5e982a8192902e1f1467)
Name to be updated?

## 3.0.0 Branch
This is the 3.0 Branch. This is currently a W.I.P

## Changelog

- Everything. The project has been rebuilt from the ground up to support composition over inheritance.

![sections](screenshots/sections.png)      ![dialog](screenshots/dialog.png)

## Download

```groovy
dependencies {
    compile 'com.twotoasters.SectionCursorAdapter:library:3.0.0'
}
```

## Basics

### DataHandlers

`DataHandler`s are a simple interface which manage the data being displayed in an adapter. Two `DataHandlers` are currently provided; `ArrayDataHandler` and `CursorDataHandler`, though it is easy to build your own. 

With the `CursorDataHandler` its easy to update the cursor `cursorDataHandler.swapCursor(cursor);`. It will then automatically notify your `CursorDataAdapter` of a change and refresh the `RecyclerView`.
 
The `ArrayDataHandler` is similarly easy. By calling the `ArrayDataHandler` and adding, inserting or removing data the adapter will get notified of the corresponding change and the `RecyclerView` will animate appropriately.

### Adapters

A few Adapters are provided by this project; `SimpleDataAdapter`, `ArrayDataAdapter`, `CursorDataAdapter`, and `SectionDataAdapter`. The implementation of these adapters is simple and rely on `DataHandler`s for data management. While these Adapters are built for the RecyclerView, they are easy to port because of their simplicity.


```java
public abstract class MyAdapter extends CursorDataAdapter<MyViewHolder> {

    public MyAdapter(CursorDataHandler dataHandler) {
        super(dataHandler);
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create your ViewHolder.
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        // CursorDataHandler returns us the cursor at the correct position.
        Cursor cursor = getItemAtPosition(position);
        
        // Bind data with your cursor.
    }
}
```

### DataWrappers and Adding Sections

`DataWrappers` are important as they have the ability to remap any data from a `DataHandler`. `SectionDataWrapper` has the ability to add sections to any `DataHandler` without mutating the core data. Building sections is done by setting a `SectionBuilder` on the `SectionDataWrapper`. Using the `SectionDataAdapter` makes it even easier by providing onCreate's and onBind's for both sections and items. _Note: it is expected that the data is presorted._

```java
public class MySectionBuilder implements SectionBuilder<String, String, ArrayDataHandler<String>> {
    
    @Nullable
    SortedMap<Integer, String> buildSections(ArrayDataHandler<String> dataHandler) {
		return null; // We don't already have a map so we'll return null to use getSectionFromItem instead.
    }

    @NonNull
    String getSectionFromItem(String item) {
    	if (item == null || item.isEmpty()) return null;

    	return item.substring(0, 1).toUpperCase();
    }
}
```

## Advanced

### Building Section Maps

Providing a map may work better for your architecture or mapping on a background thread maybe necessary if you have a large number of cursor results. Here we're mapping in the `SectionBuilder` but you can also map before updating your `DataHandler`. The following is an example of building a simple alphabetical map with a `CursorDataHandler`.

```java
public class MySectionBuilder implements SectionBuilder<String, Cursor, CursorDataHandler> {
    
    @Nullable
    SortedMap<Integer, String> buildSections(CursorDataHandler dataHandler) {
	    TreeMap<Integer, Object> sections = new TreeMap<>();
	    int columnIndex = cursor.getColumnIndex(StoreModel.NAME);
	        
	    for (int i = 0; i < dataHandler.getItemCount(); i++) {
    		Cursor cursor = dataHandler.getItemAtPosition(i);

	        String name = cursor.getString(columnIndex);
	        String section = name.substring(0, 1).toUpperCase();
	        if (!sections.containsValue(section)) {
	            sections.put(cursorPosition + sections.size(), section);
	        }
	    }
	    return sections;
    }

    @NonNull
    String getSectionFromItem(Cursor item) {
    	return ""; // This will not get called as we are providing a map in buildSections.
    }
}
```

## License

    Copyright 2016 Ticketmaster
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
       
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
