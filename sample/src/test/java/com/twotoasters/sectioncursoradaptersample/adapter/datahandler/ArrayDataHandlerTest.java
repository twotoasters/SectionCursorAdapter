package com.twotoasters.sectioncursoradaptersample.adapter.datahandler;

import android.support.v7.widget.RecyclerView.ViewHolder;

import com.twotoasters.sectioncursoradapter.adapter.ArrayDataAdapter;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.ArrayDataHandler;
import com.twotoasters.sectioncursoradaptersample.adapter.Testable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class ArrayDataHandlerTest extends Testable {

    private static final List<String> DATA;

    static {
        DATA = new ArrayList<>();
        DATA.add("Hello");
        DATA.add("Android");
        DATA.add("!");
    }

    @Mock ArrayDataAdapter<String, ViewHolder> adapter;
    ArrayDataHandler<String> dataHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dataHandler = new ArrayDataHandler<>(DATA, adapter);
    }

    @Test
    public void testAddAll() throws Exception {
        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size());

        dataHandler.addAll(DATA);

        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size() * 2);
        verify(adapter).notifyItemRangeInserted(DATA.size(), DATA.size());
    }

    @Test
    public void testItemInsert() throws Exception {
        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size());

        dataHandler.insert("Lollipop", 1);

        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size() + 1);
        assertThat(dataHandler.getItemAtPosition(1)).isEqualTo("Lollipop");
        verify(adapter).notifyItemInserted(1);
    }

    @Test
    public void testInsertRange() throws Exception {
        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size());

        List<String> list = new ArrayList<>();
        list.add("Lollipop");
        list.add("Marshmallow");
        dataHandler.insertAll(list, 1);

        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size() + 2);
        assertThat(dataHandler.getItemAtPosition(1)).isEqualTo("Lollipop");
        assertThat(dataHandler.getItemAtPosition(2)).isEqualTo("Marshmallow");
        verify(adapter).notifyItemRangeInserted(1, 2);
    }

    @Test
    public void testRemoveObject() throws Exception {
        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size());

        dataHandler.remove("Android");

        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size() - 1);
        verify(adapter).notifyItemRemoved(1);
    }

    @Test
    public void testRemoveRange() throws Exception {
        assertThat(dataHandler.getItemCount()).isEqualTo(DATA.size());

        dataHandler.removeRange(1, 3);

        assertThat(dataHandler.getItemCount()).isEqualTo(1);
        verify(adapter).notifyItemRangeRemoved(1, 2);
    }
}
