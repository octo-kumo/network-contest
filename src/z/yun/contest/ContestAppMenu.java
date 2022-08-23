package z.yun.contest;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import z.yun.contest.observable.Observable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;
import java.util.prefs.Preferences;

public class ContestAppMenu extends JMenuBar {
    public static Preferences pref = Preferences.userNodeForPackage(ContestAppMenu.class);
    public static HashMap<String, String> MAP = new HashMap<>();
    public static HashMap<String, String> MAT_MAP = new HashMap<>();

    static {

        MAP.put("Arc", "com.formdev.flatlaf.intellijthemes.FlatArcIJTheme");
        MAP.put("Arc - Orange", "com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme");
        MAP.put("Arc Dark", "com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme");
        MAP.put("Arc Dark - Orange", "com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme");
        MAP.put("Carbon", "com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme");
        MAP.put("Cobalt 2", "com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme");
        MAP.put("Cyan light", "com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme");
        MAP.put("Dark Flat", "com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme");
        MAP.put("Dark purple", "com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme");
        MAP.put("Dracula", "com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme");
        MAP.put("Gradianto Dark Fuchsia", "com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme");
        MAP.put("Gradianto Deep Ocean", "com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme");
        MAP.put("Gradianto Midnight Blue", "com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme");
        MAP.put("Gradianto Nature Green", "com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme");
        MAP.put("Gray", "com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme");
        MAP.put("Gruvbox Dark Hard", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme");
        MAP.put("Gruvbox Dark Medium", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme");
        MAP.put("Gruvbox Dark Soft", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme");
        MAP.put("Hiberbee Dark", "com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme");
        MAP.put("High contrast", "com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme");
        MAP.put("Light Flat", "com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme");
        MAP.put("Material Design Dark", "com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme");
        MAP.put("Monocai", "com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme");
        MAP.put("Monokai Pro", "com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme");
        MAP.put("Nord", "com.formdev.flatlaf.intellijthemes.FlatNordIJTheme");
        MAP.put("One Dark", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
        MAP.put("Solarized Dark", "com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme");
        MAP.put("Solarized Light", "com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme");
        MAP.put("Spacegray", "com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme");
        MAP.put("Vuesion", "com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme");
        MAP.put("Xcode-Dark", "com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme");

        MAT_MAP.put("Arc Dark (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme");
        MAT_MAP.put("Arc Dark Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme");
        MAT_MAP.put("Atom One Dark (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme");
        MAT_MAP.put("Atom One Dark Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme");
        MAT_MAP.put("Atom One Light (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme");
        MAT_MAP.put("Atom One Light Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightContrastIJTheme");
        MAT_MAP.put("Dracula (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme");
        MAT_MAP.put("Dracula Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaContrastIJTheme");
        MAT_MAP.put("GitHub (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme");
        MAT_MAP.put("GitHub Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubContrastIJTheme");
        MAT_MAP.put("GitHub Dark (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme");
        MAT_MAP.put("GitHub Dark Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme");
        MAT_MAP.put("Light Owl (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme");
        MAT_MAP.put("Light Owl Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlContrastIJTheme");
        MAT_MAP.put("Material Darker (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme");
        MAT_MAP.put("Material Darker Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme");
        MAT_MAP.put("Material Deep Ocean (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme");
        MAT_MAP.put("Material Deep Ocean Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme");
        MAT_MAP.put("Material Lighter (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme");
        MAT_MAP.put("Material Lighter Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterContrastIJTheme");
        MAT_MAP.put("Material Oceanic (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme");
        MAT_MAP.put("Material Oceanic Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme");
        MAT_MAP.put("Material Palenight (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme");
        MAT_MAP.put("Material Palenight Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightContrastIJTheme");
        MAT_MAP.put("Monokai Pro (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme");
        MAT_MAP.put("Monokai Pro Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProContrastIJTheme");
        MAT_MAP.put("Moonlight (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme");
        MAT_MAP.put("Moonlight Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightContrastIJTheme");
        MAT_MAP.put("Night Owl (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlIJTheme");
        MAT_MAP.put("Night Owl Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlContrastIJTheme");
        MAT_MAP.put("Solarized Dark (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme");
        MAT_MAP.put("Solarized Dark Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkContrastIJTheme");
        MAT_MAP.put("Solarized Light (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme");
        MAT_MAP.put("Solarized Light Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightContrastIJTheme");
    }

    public static Observable<String> theme = new Observable<>(pref.get("theme", "Dracula"));

    public static void init() {
        try {
            String theme = MAP.get(ContestAppMenu.theme.get());
            if (!theme.startsWith("com.formdev.flatlaf.intellijthemes")) throw new RuntimeException("Incorrect theme!");
            Method method = Class.forName(theme).getMethod("setup");
            method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update() {
        try {
            String theme = MAP.getOrDefault(ContestAppMenu.theme.get(), MAT_MAP.get(ContestAppMenu.theme.get()));
            FlatAnimatedLafChange.showSnapshot();
            UIManager.setLookAndFeel(theme);
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        } catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public ContestAppMenu() {
        ButtonGroup group = new ButtonGroup();
        add(new JMenu("Theme") {{
            add(new JMenu("IntelliJ") {{
                for (String name : MAP.keySet()) {
                    JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name);
                    menuItem.addItemListener(e -> {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            theme.set(name);
                            ContestAppMenu.update();
                        }
                    });
                    menuItem.setSelected(Objects.equals(theme.get(), name));
                    group.add(menuItem);
                    add(menuItem);
                }
            }});
            add(new JMenu("Material") {{
                ButtonGroup group = new ButtonGroup();
                for (String name : MAT_MAP.keySet()) {
                    JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name);
                    menuItem.addItemListener(e -> {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            theme.set(name);
                            ContestAppMenu.update();
                        }
                    });
                    menuItem.setSelected(Objects.equals(theme.get(), name));
                    group.add(menuItem);
                    add(menuItem);
                }
            }});
        }});
    }
}
