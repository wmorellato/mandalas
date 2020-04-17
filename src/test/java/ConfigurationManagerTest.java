/**
Mandalas is an open-source Minecraft plugin.
Copyright (C) 2020  Wesley Morellato

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
**/

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import com.wmorellato.mandalas.components.ElementType;
import com.wmorellato.mandalas.config.ConfigurationManager;
import com.wmorellato.mandalas.exceptions.InvalidCurveRangeException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConfigurationManagerTest {
    static ConfigurationManager config;

    @BeforeAll
    static void setupConfigurationManager() {
        YamlConfiguration fc = new YamlConfiguration();

        try {
            fc.load(new File("src\\test\\resources\\config.yml"));
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }

        config = new ConfigurationManager(fc);
    }

    @Test
    void shouldGetCurveVertices() {
        int concV = config.getCurveVerticesFromPool(ElementType.CURVE_CONCAVE);
        int randV = config.getCurveVerticesFromPool(ElementType.CURVE_RANDOM);

        assertEquals(100, concV, "Did not get correct vertices count for convex curve");
        assertEquals(5, randV, "Did not get correct vertices count for random curve");
    }

    @Test
    void shouldThrowInvalidRangeException() throws InvalidCurveRangeException {
        double[] randV = config.getRangeFromPool(ElementType.CURVE_RANDOM);
        assertEquals(0.5, randV[0], "Did not get correct lower range for random curve");
        assertEquals(0.6, randV[1], "Did not get correct higher range for random curve");

        assertThrows(InvalidCurveRangeException.class, () -> {
            config.getRangeFromPool(ElementType.CURVE_CONVEX);
        });
    }

    @Test
    void shouldGetCorrectFixedElementsAttr() throws InvalidCurveRangeException {
        double[] p1 = config.getRangeFromFixedElement("PETAL_1");
        assertEquals(0.5, p1[0], "Did not get correct lower range for PETAL_1");
        assertEquals(0.75, p1[1], "Did not get correct higher range for PETAL_1");

        double[] p2 = config.getRangeFromFixedElement("PETAL_2");
        assertEquals(0.1, p2[0], "Did not get correct lower range for PETAL_2");
        assertEquals(0.5, p2[1], "Did not get correct higher range for PETAL_2");
    }

    @Test
    void shouldGetNumberOfElements() {
        int numRandom = config.getNumberOfRandomElements();
        assertEquals(8, numRandom, "Incorrect number of random elements");

        int numFixed = config.getNumberOfFixedElements();
        assertEquals(3, numFixed, "Incorrect number of fixed elements");
    }
}