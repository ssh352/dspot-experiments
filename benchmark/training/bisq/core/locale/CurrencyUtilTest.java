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
package bisq.core.locale;


import BaseCurrencyNetwork.BTC_MAINNET;
import BaseCurrencyNetwork.BTC_REGTEST;
import BaseCurrencyNetwork.BTC_TESTNET;
import Coin.Network.MAINNET;
import Coin.Network.REGTEST;
import Coin.Network.TESTNET;
import bisq.asset.Asset;
import bisq.asset.AssetRegistry;
import bisq.asset.Coin;
import bisq.asset.coins.Ether;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class CurrencyUtilTest {
    @Test
    public void testGetTradeCurrency() {
        Optional<TradeCurrency> euro = CurrencyUtil.getTradeCurrency("EUR");
        Optional<TradeCurrency> naira = CurrencyUtil.getTradeCurrency("NGN");
        Optional<TradeCurrency> fake = CurrencyUtil.getTradeCurrency("FAK");
        Assert.assertTrue(euro.isPresent());
        Assert.assertTrue(naira.isPresent());
        Assert.assertFalse("Fake currency shouldn't exist", fake.isPresent());
    }

    @Test
    public void testFindAsset() {
        CurrencyUtilTest.MockAssetRegistry assetRegistry = new CurrencyUtilTest.MockAssetRegistry();
        // test if code is matching
        boolean daoTradingActivated = false;
        // Test if BSQ on mainnet is failing
        Assert.assertFalse(CurrencyUtil.findAsset(assetRegistry, "BSQ", BTC_MAINNET, daoTradingActivated).isPresent());
        // on testnet/regtest it is allowed
        Assert.assertEquals(CurrencyUtil.findAsset(assetRegistry, "BSQ", BTC_TESTNET, daoTradingActivated).get().getTickerSymbol(), "BSQ");
        daoTradingActivated = true;
        // With daoTradingActivated we can request BSQ
        Assert.assertEquals(CurrencyUtil.findAsset(assetRegistry, "BSQ", BTC_MAINNET, daoTradingActivated).get().getTickerSymbol(), "BSQ");
        // Test if not matching ticker is failing
        Assert.assertFalse(CurrencyUtil.findAsset(assetRegistry, "BSQ1", BTC_MAINNET, daoTradingActivated).isPresent());
        // Add a mock coin which has no mainnet version, needs to fail if we are on mainnet
        MockTestnetCoin.Testnet mockTestnetCoin = new MockTestnetCoin.Testnet();
        try {
            assetRegistry.addAsset(mockTestnetCoin);
            CurrencyUtil.findAsset(assetRegistry, "MOCK_COIN", BTC_MAINNET, daoTradingActivated);
            Assert.fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            String wantMessage = "We are on mainnet and we could not find an asset with network type mainnet";
            Assert.assertTrue(((((("Unexpected exception, want message starting with " + "'") + wantMessage) + "', got '") + (e.getMessage())) + "'"), e.getMessage().startsWith(wantMessage));
        }
        // For testnet its ok
        Assert.assertEquals(CurrencyUtil.findAsset(assetRegistry, "MOCK_COIN", BTC_TESTNET, daoTradingActivated).get().getTickerSymbol(), "MOCK_COIN");
        Assert.assertEquals(TESTNET, getNetwork());
        // For regtest its still found
        Assert.assertEquals(CurrencyUtil.findAsset(assetRegistry, "MOCK_COIN", BTC_REGTEST, daoTradingActivated).get().getTickerSymbol(), "MOCK_COIN");
        // We test if we are not on mainnet to get the mainnet coin
        Coin ether = new Ether();
        Assert.assertEquals(CurrencyUtil.findAsset(assetRegistry, "ETH", BTC_TESTNET, daoTradingActivated).get().getTickerSymbol(), "ETH");
        Assert.assertEquals(CurrencyUtil.findAsset(assetRegistry, "ETH", BTC_REGTEST, daoTradingActivated).get().getTickerSymbol(), "ETH");
        Assert.assertEquals(MAINNET, ether.getNetwork());
        // We test if network matches exactly if there are distinct network types defined like with BSQ
        Coin bsq = ((Coin) (CurrencyUtil.findAsset(assetRegistry, "BSQ", BTC_MAINNET, daoTradingActivated).get()));
        Assert.assertEquals("BSQ", bsq.getTickerSymbol());
        Assert.assertEquals(MAINNET, bsq.getNetwork());
        bsq = ((Coin) (CurrencyUtil.findAsset(assetRegistry, "BSQ", BTC_TESTNET, daoTradingActivated).get()));
        Assert.assertEquals("BSQ", bsq.getTickerSymbol());
        Assert.assertEquals(TESTNET, bsq.getNetwork());
        bsq = ((Coin) (CurrencyUtil.findAsset(assetRegistry, "BSQ", BTC_REGTEST, daoTradingActivated).get()));
        Assert.assertEquals("BSQ", bsq.getTickerSymbol());
        Assert.assertEquals(REGTEST, bsq.getNetwork());
    }

    @Test
    public void testGetNameAndCodeOfRemovedAsset() {
        Assert.assertEquals("Bitcoin Cash (BCH)", CurrencyUtil.getNameAndCode("BCH"));
        Assert.assertEquals("N/A (XYZ)", CurrencyUtil.getNameAndCode("XYZ"));
    }

    class MockAssetRegistry extends AssetRegistry {
        private List<Asset> registeredAssets = new ArrayList<>();

        MockAssetRegistry() {
            for (Asset asset : ServiceLoader.load(Asset.class)) {
                registeredAssets.add(asset);
            }
        }

        void addAsset(Asset asset) {
            registeredAssets.add(asset);
        }

        public Stream<Asset> stream() {
            return registeredAssets.stream();
        }
    }
}
