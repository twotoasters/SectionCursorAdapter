package com.twotoasters.sectioncursoradaptersample.adapter.datahandler;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.twotoasters.sectioncursoradapter.adapter.datahandler.CursorDataHandler;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper.SectionBuilder;
import com.twotoasters.sectioncursoradaptersample.adapter.Testable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class SectionDataWrapperTests extends Testable {

    private static final SortedMap<Integer, String> SECTION_MAP;
    private static final SortedMap<Integer, String> SECTION_MAP_ALT;
    private static final Object[] FAST_SCROLL_OBJECTS;

    static {
        SECTION_MAP = new TreeMap<>();
        SECTION_MAP.put(0, "A");
        SECTION_MAP.put(3, "B");
        SECTION_MAP.put(6, "C");

        SECTION_MAP_ALT = new TreeMap<>();
        SECTION_MAP_ALT.put(2, "A");
        SECTION_MAP_ALT.put(4, "B");
        SECTION_MAP_ALT.put(5, "C");

        FAST_SCROLL_OBJECTS = new Object[3];
        FAST_SCROLL_OBJECTS[0] = "A";
        FAST_SCROLL_OBJECTS[1] = "B";
        FAST_SCROLL_OBJECTS[2] = "C";
    }

    @Mock Cursor cursor;
    SectionDataWrapper<String, Cursor, CursorDataHandler> dataWrapper;
    TestSectionBuilder sectionBuilder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(cursor.getPosition()).thenReturn(0);
        when(cursor.getCount()).thenReturn(10);
        when(cursor.moveToPosition(anyInt())).thenReturn(true);

        dataWrapper = new SectionDataWrapper<>(new CursorDataHandler(cursor, 0));

        sectionBuilder = new TestSectionBuilder();
        sectionBuilder.setSections(SECTION_MAP);
        dataWrapper.setSectionBuilder(sectionBuilder);
    }

    @Test
    public void itShouldCheckIfSection() {
        assertThat(dataWrapper.isSection(0)).isTrue();
        assertThat(dataWrapper.isSection(1)).isFalse();
        assertThat(dataWrapper.isSection(2)).isFalse();
        assertThat(dataWrapper.isSection(3)).isTrue();
        assertThat(dataWrapper.isSection(4)).isFalse();
        assertThat(dataWrapper.isSection(5)).isFalse();
        assertThat(dataWrapper.isSection(6)).isTrue();
        assertThat(dataWrapper.isSection(7)).isFalse();
    }

    @Test
    public void itShouldBindWithOneSection() {
        SortedMap<Integer, String> map = new TreeMap<>();
        map.put(0, "Hello");

        sectionBuilder.setSections(map);
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getItemCount()).isEqualTo(11);
        // Testing sections
        assertThat(dataWrapper.getSectionPosition(0)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(1)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(2)).isEqualTo(0);
        // Testing items
        assertThat(dataWrapper.getItemAtPosition(0)).isNull();
        assertThat(dataWrapper.getItemAtPosition(1)).isSameAs(cursor);
        assertThat(dataWrapper.getItemAtPosition(2)).isSameAs(cursor);
    }

    @Test
    public void itShouldGetIndexWithinSections() {
        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getSectionPosition(0)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(1)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(2)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(3)).isEqualTo(1);
        assertThat(dataWrapper.getSectionPosition(4)).isEqualTo(1);
        assertThat(dataWrapper.getSectionPosition(5)).isEqualTo(1);
        assertThat(dataWrapper.getSectionPosition(6)).isEqualTo(2);
        assertThat(dataWrapper.getSectionPosition(7)).isEqualTo(2);

        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP_ALT));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getSectionPosition(0)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(1)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(2)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(3)).isEqualTo(0);
        assertThat(dataWrapper.getSectionPosition(4)).isEqualTo(1);
        assertThat(dataWrapper.getSectionPosition(5)).isEqualTo(2);
        assertThat(dataWrapper.getSectionPosition(6)).isEqualTo(2);
    }

    @Test
    public void itShouldGetListPositionWithoutSections() {
        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getWrappedPosition(0)).isEqualTo(RecyclerView.NO_POSITION);
        assertThat(dataWrapper.getWrappedPosition(1)).isEqualTo(0);
        assertThat(dataWrapper.getWrappedPosition(2)).isEqualTo(1);
        assertThat(dataWrapper.getWrappedPosition(3)).isEqualTo(RecyclerView.NO_POSITION);
        assertThat(dataWrapper.getWrappedPosition(4)).isEqualTo(2);
        assertThat(dataWrapper.getWrappedPosition(5)).isEqualTo(3);
        assertThat(dataWrapper.getWrappedPosition(6)).isEqualTo(RecyclerView.NO_POSITION);
        assertThat(dataWrapper.getWrappedPosition(7)).isEqualTo(4);

        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP_ALT));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getWrappedPosition(0)).isEqualTo(0);
        assertThat(dataWrapper.getWrappedPosition(1)).isEqualTo(1);
        assertThat(dataWrapper.getWrappedPosition(2)).isEqualTo(RecyclerView.NO_POSITION);
        assertThat(dataWrapper.getWrappedPosition(3)).isEqualTo(2);
        assertThat(dataWrapper.getWrappedPosition(4)).isEqualTo(RecyclerView.NO_POSITION);
        assertThat(dataWrapper.getWrappedPosition(5)).isEqualTo(RecyclerView.NO_POSITION);
        assertThat(dataWrapper.getWrappedPosition(6)).isEqualTo(3);
        assertThat(dataWrapper.getWrappedPosition(7)).isEqualTo(4);

        sectionBuilder.setSections(new TreeMap<Integer, String>());
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getWrappedPosition(0)).isEqualTo(0);
        assertThat(dataWrapper.getWrappedPosition(1)).isEqualTo(1);
        assertThat(dataWrapper.getWrappedPosition(2)).isEqualTo(2);
    }

    // ********** Fast Scroll SectionIndexer Tests **********/

    @Test
    public void itShouldGetPositionForSection() {
        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getPositionForSection(0)).isEqualTo(0);
        assertThat(dataWrapper.getPositionForSection(1)).isEqualTo(3);
        assertThat(dataWrapper.getPositionForSection(2)).isEqualTo(6);

        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP_ALT));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getPositionForSection(0)).isEqualTo(2);
        assertThat(dataWrapper.getPositionForSection(1)).isEqualTo(4);
        assertThat(dataWrapper.getPositionForSection(2)).isEqualTo(5);
    }

    @Test
    public void itShouldGetSectionForPosition() {
        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getSectionForPosition(0)).isEqualTo(0);
        assertThat(dataWrapper.getSectionForPosition(1)).isEqualTo(0);
        assertThat(dataWrapper.getSectionForPosition(2)).isEqualTo(0);
        assertThat(dataWrapper.getSectionForPosition(3)).isEqualTo(1);
        assertThat(dataWrapper.getSectionForPosition(4)).isEqualTo(1);
        assertThat(dataWrapper.getSectionForPosition(5)).isEqualTo(1);
        assertThat(dataWrapper.getSectionForPosition(6)).isEqualTo(2);
        assertThat(dataWrapper.getSectionForPosition(7)).isEqualTo(2);

        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP_ALT));
        dataWrapper.onDataChanged();
        assertThat(dataWrapper.getSectionForPosition(0)).isEqualTo(0);
        assertThat(dataWrapper.getSectionForPosition(1)).isEqualTo(0);
        assertThat(dataWrapper.getSectionForPosition(2)).isEqualTo(0);
        assertThat(dataWrapper.getSectionForPosition(3)).isEqualTo(0);
        assertThat(dataWrapper.getSectionForPosition(4)).isEqualTo(1);
        assertThat(dataWrapper.getSectionForPosition(5)).isEqualTo(2);
        assertThat(dataWrapper.getSectionForPosition(6)).isEqualTo(2);
        assertThat(dataWrapper.getSectionForPosition(7)).isEqualTo(2);
    }

    @Test
    public void itShouldGetFastScrollSections() {
        Object[] fastScrollObjects;

        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP));
        dataWrapper.onDataChanged();
        fastScrollObjects = dataWrapper.getSections();
        assertThat(fastScrollObjects).isNotNull();
        assertThat(fastScrollObjects.length).isEqualTo(3);
        assertThat(fastScrollObjects[0]).isEqualTo("A");
        assertThat(fastScrollObjects[1]).isEqualTo("B");
        assertThat(fastScrollObjects[2]).isEqualTo("C");

        sectionBuilder.setSections(new TreeMap<>(SECTION_MAP_ALT));
        dataWrapper.onDataChanged();
        fastScrollObjects = dataWrapper.getSections();
        assertThat(fastScrollObjects).isNotNull();
        assertThat(fastScrollObjects.length).isEqualTo(3);
        assertThat(fastScrollObjects[0]).isEqualTo("A");
        assertThat(fastScrollObjects[1]).isEqualTo("B");
        assertThat(fastScrollObjects[2]).isEqualTo("C");
    }

    private static class TestSectionBuilder implements SectionBuilder<String, Cursor, CursorDataHandler> {
        public SortedMap<Integer, String> sections;

        public void setSections(SortedMap<Integer, String> sections) {
            this.sections = sections;
        }

        @Nullable
        @Override
        public SortedMap<Integer, String> buildSections(CursorDataHandler dataHandler) {
            return sections;
        }

        @NonNull
        @Override
        public String getSectionFromItem(Cursor item) {
            return null;
        }
    }
}
