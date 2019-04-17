/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.core.offer;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(OfferPayload.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class OfferTest {
    @Test
    public void testHasNoRange() {
        OfferPayload payload = Mockito.mock(OfferPayload.class);
        Mockito.when(payload.getMinAmount()).thenReturn(1000L);
        Mockito.when(payload.getAmount()).thenReturn(1000L);
        Offer offer = new Offer(payload);
        Assert.assertFalse(offer.isRange());
    }

    @Test
    public void testHasRange() {
        OfferPayload payload = Mockito.mock(OfferPayload.class);
        Mockito.when(payload.getMinAmount()).thenReturn(1000L);
        Mockito.when(payload.getAmount()).thenReturn(2000L);
        Offer offer = new Offer(payload);
        Assert.assertTrue(offer.isRange());
    }
}
