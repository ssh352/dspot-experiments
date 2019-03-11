package org.robolectric.res;


import ResType.CHAR_SEQUENCE;
import ResType.DRAWABLE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.res.android.ResTable_config;


@RunWith(JUnit4.class)
public class ResourceParserTest {
    private ResourceTable resourceTable;

    private ResTable_config config;

    @Test
    public void shouldLoadDrawableXmlResources() {
        TypedResource value = resourceTable.getValue(new ResName("org.robolectric", "drawable", "rainbow"), config);
        assertThat(value).isNotNull();
        assertThat(value.getResType()).isEqualTo(DRAWABLE);
        assertThat(value.isFile()).isTrue();
        assertThat(((String) (value.getData()))).contains("rainbow.xml");
    }

    @Test
    public void shouldLoadDrawableBitmapResources() {
        TypedResource value = resourceTable.getValue(new ResName("org.robolectric", "drawable", "an_image"), config);
        assertThat(value).isNotNull();
        assertThat(value.getResType()).isEqualTo(DRAWABLE);
        assertThat(value.isFile()).isTrue();
        assertThat(((String) (value.getData()))).contains("an_image.png");
    }

    @Test
    public void shouldLoadDrawableBitmapResourcesDefinedByItemTag() {
        TypedResource value = resourceTable.getValue(new ResName("org.robolectric", "drawable", "example_item_drawable"), config);
        assertThat(value).isNotNull();
        assertThat(value.getResType()).isEqualTo(DRAWABLE);
        assertThat(value.isReference()).isTrue();
        assertThat(((String) (value.getData()))).isEqualTo("@drawable/an_image");
    }

    @Test
    public void shouldLoadIdResourcesDefinedByItemTag() {
        TypedResource value = resourceTable.getValue(new ResName("org.robolectric", "id", "id_declared_in_item_tag"), config);
        assertThat(value).isNotNull();
        assertThat(value.getResType()).isEqualTo(CHAR_SEQUENCE);
        assertThat(value.isReference()).isFalse();
        assertThat(value.asString()).isEmpty();
        assertThat(((String) (value.getData()))).isEmpty();
    }

    @Test
    public void whenIdItemsHaveStringContent_shouldLoadIdResourcesDefinedByItemTag() {
        TypedResource value = resourceTable.getValue(new ResName("org.robolectric", "id", "id_with_string_value"), config);
        assertThat(value.asString()).isEmpty();
    }
}

