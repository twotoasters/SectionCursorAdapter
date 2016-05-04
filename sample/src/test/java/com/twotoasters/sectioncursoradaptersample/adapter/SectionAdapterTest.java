package com.twotoasters.sectioncursoradaptersample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import com.twotoasters.sectioncursoradapter.adapter.SectionArrayAdapter;
import com.twotoasters.sectioncursoradaptersample.SampleApp;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAdapterTest extends Testable {

    private TestAdapter adapter;

    @Before
    public void setUp() {
        adapter = new TestAdapter(SampleApp.getInstance());
    }

    @Test
    public void itShouldBindWithoutSections() {
        LinkedHashMap<Object, Object[]> map = new LinkedHashMap<>();
        map.put(null, new Object[3]);

        adapter.setDataMapWithArray(map);
        assertThat(adapter.getCount()).isEqualTo(3);
        // Testing positions
        assertThat(adapter.getSectionPosition(0)).isEqualTo(SectionArrayAdapter.POSITION_NOT_FOUND);
        assertThat(adapter.getItemPosition(0)).isEqualTo(0);
        assertThat(adapter.getItemPosition(1)).isEqualTo(1);
        assertThat(adapter.getItemPosition(2)).isEqualTo(2);
    }

    @Test
    public void itShouldNotBeFirstButShouldBeLast() {
        LinkedHashMap<Object, Object[]> map = new LinkedHashMap<>();
        map.put(null, new Object[1]);
        map.put("Hello", new Object[2]);
        map.put("World", new Object[0]);

        adapter.setDataMapWithArray(map);
        assertThat(adapter.getCount()).isEqualTo(5);
        // Testing sections
        assertThat(adapter.getSectionPosition(0)).isEqualTo(SectionArrayAdapter.POSITION_NOT_FOUND);
        assertThat(adapter.getSectionPosition(1)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(2)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(3)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(4)).isEqualTo(1);
        // Testing items
        assertThat(adapter.getItemPosition(0)).isEqualTo(0);
        assertThat(adapter.getItemPosition(1)).isEqualTo(SectionArrayAdapter.POSITION_NOT_FOUND);
        assertThat(adapter.getItemPosition(2)).isEqualTo(0);
        assertThat(adapter.getItemPosition(3)).isEqualTo(1);
        assertThat(adapter.getItemPosition(4)).isEqualTo(SectionArrayAdapter.POSITION_NOT_FOUND);
    }

    @Test
    public void itShouldBindWithOneSection() {
        LinkedHashMap<Object, Object[]> map = new LinkedHashMap<>();
        map.put("Hello", new Object[2]);

        adapter.setDataMapWithArray(map);
        assertThat(adapter.getCount()).isEqualTo(3);
        // Testing sections
        assertThat(adapter.getSectionPosition(0)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(1)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(2)).isEqualTo(0);
        // Testing items
        assertThat(adapter.getItemPosition(0)).isEqualTo(SectionArrayAdapter.POSITION_NOT_FOUND);
        assertThat(adapter.getItemPosition(1)).isEqualTo(0);
        assertThat(adapter.getItemPosition(2)).isEqualTo(1);
    }

    @Test
    public void itShouldBindWithMultipleSections() {
        LinkedHashMap<Object, Object[]> map = new LinkedHashMap<>();
        map.put("Hello", new Object[2]);
        map.put("World", new Object[2]);

        adapter.setDataMapWithArray(map);
        assertThat(adapter.getCount()).isEqualTo(6);
        // Testing sections
        assertThat(adapter.getSectionPosition(0)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(1)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(2)).isEqualTo(0);
        assertThat(adapter.getSectionPosition(3)).isEqualTo(1);
        assertThat(adapter.getSectionPosition(4)).isEqualTo(1);
        assertThat(adapter.getSectionPosition(5)).isEqualTo(1);
        // Testing items
        assertThat(adapter.getItemPosition(0)).isEqualTo(SectionArrayAdapter.POSITION_NOT_FOUND);
        assertThat(adapter.getItemPosition(1)).isEqualTo(0);
        assertThat(adapter.getItemPosition(2)).isEqualTo(1);
        assertThat(adapter.getItemPosition(3)).isEqualTo(SectionArrayAdapter.POSITION_NOT_FOUND);
        assertThat(adapter.getItemPosition(4)).isEqualTo(0);
        assertThat(adapter.getItemPosition(5)).isEqualTo(1);
    }

    @Test
    public void itShouldReturnCorrectItems() {
        LinkedHashMap<Object, Object[]> map = new LinkedHashMap<>();
        String section1 = "Hello";
        String section2 = "World";
        String item1 = "I";
        String item2 = " love ";
        String item3 = "Android!";

        map.put(section1, new Object[] {item1 });
        map.put(section2, new Object[] {item2, item3 });

        adapter.setDataMapWithArray(map);
        assertThat(adapter.getCount()).isEqualTo(5);
        // Testing sections
        assertThat(adapter.getSection(0)).isEqualTo(section1);
        assertThat(adapter.getSection(1)).isEqualTo(section2);
        assertThat(adapter.getSection(2)).isNull();
        // Testing items
        assertThat(adapter.getItem(0, 0)).isEqualTo(item1);
        assertThat(adapter.getItem(0, 1)).isNull();
        assertThat(adapter.getItem(1, 0)).isEqualTo(item2);
        assertThat(adapter.getItem(1, 1)).isEqualTo(item3);
        assertThat(adapter.getItem(1, 2)).isNull();
        assertThat(adapter.getItem(2, 0)).isNull();
        // Testing all objects
        assertThat(adapter.getItem(0)).isEqualTo(section1);
        assertThat(adapter.getItem(1)).isEqualTo(item1);
        assertThat(adapter.getItem(2)).isEqualTo(section2);
        assertThat(adapter.getItem(3)).isEqualTo(item2);
        assertThat(adapter.getItem(4)).isEqualTo(item3);
        assertThat(adapter.getItem(5)).isNull();
    }

    private static class TestAdapter extends SectionArrayAdapter {
        public TestAdapter(Context context) {
            super(context, 0 , 0);
        }

        @Override
        protected Object getSectionFromItem(Object item) {
            return null;
        }

        @Override
        protected ViewHolder createSectionViewHolder(View sectionView, Object section) {
            return null;
        }

        @Override
        protected void bindSectionViewHolder(int sectionPosition, ViewHolder sectionViewHolder, ViewGroup parent, Object section) {

        }

        @Override
        protected ViewHolder createItemViewHolder(int sectionPosition, View itemView, Object item) {
            return null;
        }

        @Override
        protected void bindItemViewHolder(int sectionPosition, int itemPosition, ViewHolder itemViewHolder, ViewGroup parent, Object item) {

        }
    }

}
