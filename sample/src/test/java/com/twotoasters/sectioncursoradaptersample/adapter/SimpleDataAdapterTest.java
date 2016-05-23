package com.twotoasters.sectioncursoradaptersample.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import com.twotoasters.sectioncursoradapter.adapter.SimpleDataAdapter;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.DataHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SimpleDataAdapterTest {
    @Mock private DataHandler<String> dataHandler;
    private TestAdapter adapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        adapter = new TestAdapter(dataHandler);
    }

    @Test
    public void testItShouldGetCount() throws Exception {
        when(dataHandler.getItemCount()).thenReturn(10);

        assertThat(adapter.getItemCount()).isEqualTo(10);

        verify(dataHandler).getItemCount();
    }

    @Test
    public void testHasStaleIds() throws Exception {
        verify(dataHandler).hasStableIds();
    }

    @Test
    public void testGetItemId() throws Exception {
        when(dataHandler.getItemId(5)).thenReturn(10L);

        assertThat(adapter.getItemId(5)).isEqualTo(10L);

        verify(dataHandler).getItemId(5);
    }

    private static class TestAdapter extends SimpleDataAdapter<String, DataHandler<String>,ViewHolder> {

        public TestAdapter(DataHandler<String> dataHandler) {
            super(dataHandler);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }
    }
}
