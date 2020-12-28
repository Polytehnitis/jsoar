/**
 * 
 */
package org.jsoar.performancetesting;

import java.nio.file.Path;
import java.util.List;

/**
 * A class for holding test settings. This is used all over the place for
 * getting individual test settings. It is usually constructed with a default
 * test setting object (another TestSettings object) and then any additional
 * assignments overwrite those values. This means there is less overhead for
 * trying to determine the settings for a test.
 * 
 * @author ALT
 *
 */
public class TestSettings
{
    private boolean jsoarEnabled;

    private boolean csoarEnabled;

    private int runCount;

    private int warmUpCount;

    private List<Integer> decisionCycles;

    private boolean useSeed;

    private long seed;

    private Path csvDirectory;

    private Path summaryFile;

    private List<Path> csoarDirectories;

    private List<Path> jsoarDirectories;

    private String jvmSettings;

    public TestSettings(TestSettings other)
    {
        jsoarEnabled = other.isJSoarEnabled();
        csoarEnabled = other.isCSoarEnabled();

        runCount = other.getRunCount();
        warmUpCount = other.getWarmUpCount();

        decisionCycles = other.getDecisionCycles();

        useSeed = other.isUsingSeed();
        seed = other.getSeed();

        csvDirectory = other.getCSVDirectory();
        summaryFile = other.getSummaryFile();

        csoarDirectories = other.getCSoarVersions();
        jsoarDirectories = other.getJSoarVersions();

        jvmSettings = other.getJVMSettings();
    }

    public TestSettings(boolean jsoarEnabled, boolean csoarEnabled,
            int runCount, int warmUpCount, List<Integer> decisionCycles,
            boolean useSeed, long seed, Path csvDirectory,
            Path summaryFile, List<Path> csoarDirectories,
            List<Path> jsoarDirectories, String jvmSettings)
    {
        this.jsoarEnabled = jsoarEnabled;
        this.csoarEnabled = csoarEnabled;

        this.runCount = runCount;
        this.warmUpCount = warmUpCount;

        this.decisionCycles = decisionCycles;

        this.useSeed = useSeed;
        this.seed = seed;

        this.csvDirectory = csvDirectory;
        this.summaryFile = summaryFile;

        this.csoarDirectories = csoarDirectories;
        this.jsoarDirectories = jsoarDirectories;

        this.jvmSettings = jvmSettings;

        // Sanity check
        if (this.csoarEnabled && csoarDirectories.size() == 0)
        {
            throw new RuntimeException(
                    "Sanity Check Failed!  CSoar is enabled but there are no directories specified for it!");
        }

        if (this.jsoarEnabled && jsoarDirectories.size() == 0)
        {
            throw new RuntimeException(
                    "Sanity Check Failed!  JSoar is enabled but there are no directories specified for it!");
        }
    }

    public void setJSoarEnabled(boolean jsoarEnabled)
    {
        this.jsoarEnabled = jsoarEnabled;
    }

    public boolean isJSoarEnabled()
    {
        return jsoarEnabled;
    }

    public void setCSoarEnabled(boolean csoarEnabled)
    {
        this.csoarEnabled = csoarEnabled;
    }

    public boolean isCSoarEnabled()
    {
        return csoarEnabled;
    }

    public void setRunCount(int runCount)
    {
        this.runCount = runCount;
    }

    public int getRunCount()
    {
        return runCount;
    }

    public void setWarmUpCount(int warmUpCount)
    {
        this.warmUpCount = warmUpCount;
    }

    public int getWarmUpCount()
    {
        return warmUpCount;
    }

    public void setDecisionCycles(List<Integer> decisionCycles)
    {
        this.decisionCycles = decisionCycles;
    }

    public List<Integer> getDecisionCycles()
    {
        return decisionCycles;
    }

    public void setUseSeed(boolean useSeed)
    {
        this.useSeed = useSeed;
    }

    public boolean isUsingSeed()
    {
        return this.useSeed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }

    public long getSeed()
    {
        return seed;
    }

    public void setCsvDirectory(Path csvDirectory)
    {
        this.csvDirectory = csvDirectory;
    }

    public Path getCsvDirectory()
    {
        return csvDirectory;
    }

    public void setSummaryFile(Path summaryFile)
    {
        this.summaryFile = summaryFile;
    }

    public Path getSummaryFile()
    {
        return summaryFile;
    }

    public void setCsoarVersions(List<Path> csoarDirectories)
    {
        this.csoarDirectories = csoarDirectories;
    }

    public List<Path> getCsoarVersions()
    {
        return csoarDirectories;
    }

    public void setJsoarVersions(List<Path> jsoarDirectories)
    {
        this.jsoarDirectories = jsoarDirectories;
    }

    public List<Path> getJsoarVersions()
    {
        return jsoarDirectories;
    }

    public void setJVMSettings(String jvmSettings)
    {
        this.jvmSettings = jvmSettings;
    }

    public String getJVMSettings()
    {
        return jvmSettings;
    }
}
