package br.edu.utfpr.recominer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class ProjectFilePairReleaseOcurrence {

    public static String getHeader() {
        return "Project;" // Project analysed
                + "# Versions;" // Number of Project's Versions
                + "# Distinct Cochanges" // Number of file pairs
                ;
    }

    private final Project project;
    private final Set<Version> versions;
    private final Map<FilePair, AtomicInteger> filePairs;
    private final Map<FilePair, AtomicInteger> filePairsWithMinOccurrences;
    private final Map<FilePair, FilePairReleasesOccurenceCounter> filePairReleasesOccurenceCounter;
    private final Map<FilterByApriori, Set<FilePair>> filePairsPerFilter;
    private final FilePairOcurrencesGroup filePairOcurrencesGroup;
    private final int minOccurrencesInEachVersion;
    private final int minFilePairOccurrences;
    private final List<Version> allVersions;

    public ProjectFilePairReleaseOcurrence(Project project, List<Version> allVersions,
            Collection<FilterFilePairByReleaseOcurrence> filtersOccurrences,
            Collection<FilterByApriori> filtersByApriori) {
        this.project = project;
        this.versions = new LinkedHashSet<>();
        this.filePairsWithMinOccurrences = new HashMap<>();
        this.filePairs = new HashMap<>();
        this.filePairOcurrencesGroup = new FilePairOcurrencesGroup(filtersOccurrences);
        this.filePairReleasesOccurenceCounter = new HashMap<>();
        this.minOccurrencesInEachVersion = 1;
        this.minFilePairOccurrences = 1;
        this.allVersions = allVersions;
        this.filePairsPerFilter = new LinkedHashMap<>();
        for (FilterByApriori filtersByApriori1 : filtersByApriori) {
            filePairsPerFilter.put(filtersByApriori1, new HashSet<>());
        }
    }

    public ProjectFilePairReleaseOcurrence(Project project, List<Version> allVersions, int minFilePairOccurrences, int minOccurrencesInEachVersion,
            List<FilterFilePairByReleaseOcurrence> filtersOccurrences,
            Collection<FilterByApriori> filtersByApriori) {
        this.project = project;
        this.allVersions = allVersions;
        this.minOccurrencesInEachVersion = minOccurrencesInEachVersion;
        this.minFilePairOccurrences = minFilePairOccurrences;
        this.versions = new LinkedHashSet<>();
        this.filePairsWithMinOccurrences = new HashMap<>();
        this.filePairs = new HashMap<>();
        this.filePairOcurrencesGroup = new FilePairOcurrencesGroup(filtersOccurrences);
        this.filePairReleasesOccurenceCounter = new HashMap<>();
        this.filePairsPerFilter = new LinkedHashMap<>();
        for (FilterByApriori filtersByApriori1 : filtersByApriori) {
            filePairsPerFilter.put(filtersByApriori1, new HashSet<>());
        }
    }

    public void add(ProjectFilePairReleaseOcurrence versionSummary) {
        for (Version version : versionSummary.versions) {
            for (Map.Entry<FilePair, AtomicInteger> entrySet : versionSummary.filePairs.entrySet()) {
                FilePair key = entrySet.getKey();
                AtomicInteger value = entrySet.getValue();
                addVersionForFilePair(key, version, value.get());
            }
        }
    }

    private void addVersion(Version version) {
        this.versions.add(version);
    }

    private void addFilePair(FilePair filePair) {
        if (filePairs.containsKey(filePair)) {
            filePairs.get(filePair).incrementAndGet();
        } else {
            filePairs.put(filePair, new AtomicInteger(1));
        }
    }

    public void addPairFileForAprioriFilter(FilePair filePair, FilterByApriori filterByApriori) {
        filePairsPerFilter.get(filterByApriori).add(filePair);
    }

    public void addVersionForFilePair(FilePair filePair, Version version) {
        addVersionForFilePair(filePair, version, 1);
    }

    public void addVersionForFilePair(FilePair filePair, Version version, int quantity) {
        addFilePair(filePair);
        addVersion(version);
        if (filePairReleasesOccurenceCounter.containsKey(filePair)) {
            filePairReleasesOccurenceCounter.get(filePair).addVersionOccurrence(version, quantity);
        } else {
            final FilePairReleasesOccurenceCounter counter = new FilePairReleasesOccurenceCounter(filePair, allVersions);
            counter.addVersionOccurrence(version, quantity);
            filePairReleasesOccurenceCounter.put(filePair, counter);
        }

        if (hasMinimumOccurrences(filePair)) {
            if (filePairsWithMinOccurrences.containsKey(filePair)) {
                filePairsWithMinOccurrences.get(filePair).addAndGet(quantity);
            } else {
                filePairsWithMinOccurrences.put(filePair, new AtomicInteger(quantity));
            }
        }
    }

//    public void addVersionForFilePair(Collection<FilePair> filePairs, Version version) {
//        for (FilePair filePair : filePairs) {
//            addVersionForFilePair(filePair, version);
//        }
//    }

    public boolean hasMinimumOccurrencesInOneVersion(FilePair filePair) {
        if (filePairReleasesOccurenceCounter.containsKey(filePair)) {
            return filePairReleasesOccurenceCounter.get(filePair).hasAtLeastOccurrencesInOneVersion(minOccurrencesInEachVersion);
        }
        return false;
    }

    /**
     * Number of pair file changes
     *
     * @param filePair
     * @return
     */
    public int getOccurrences(FilePair filePair) {
        if (filePairs.containsKey(filePair)) {
            return filePairs.get(filePair).get();
        }
        return 0;
    }

    public boolean hasMinimumOccurrences(FilePair filePair) {
        assert (filePairs.containsKey(filePair)) : "Arquivo deveria ter sido adicionado antes.";
        return getOccurrences(filePair) >= minOccurrencesInEachVersion;
    }

    /**
     * Number of pair file changes
     *
     * @param filePair
     * @return
     */
    public int getVersionSequenceOccurrences(FilePair filePair) {
        if (filePairs.containsKey(filePair)) {
            return filePairs.get(filePair).get();
        }
        return 0;
    }

    public FilePairOcurrencesGroup getFilePairOcurrencesGroup() {
        return filePairOcurrencesGroup;
    }

    public Map<FilePair, FilePairReleasesOccurenceCounter> getFilePairReleasesOccurenceCounter() {
        return Collections.unmodifiableMap(filePairReleasesOccurenceCounter);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.project);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProjectFilePairReleaseOcurrence other = (ProjectFilePairReleaseOcurrence) obj;
        if (!Objects.equals(this.project, other.project)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(project).append(";");
        sb.append(versions.size()).append(";");
        sb.append(filePairs.size());
        filePairOcurrencesGroup.groupFilePairs(new ArrayList<>(versions), filePairReleasesOccurenceCounter.values(), minOccurrencesInEachVersion);
        sb.append(";").append(filePairOcurrencesGroup.toString());
        for (Map.Entry<FilterByApriori, Set<FilePair>> filePairPerFilter : filePairsPerFilter.entrySet()) {
            sb.append(";").append(filePairPerFilter.getValue().size());
        }
        return sb.toString();
    }

    public String getDynamicHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFilePairOcurrencesGroup().getDynamicHeader());
        for (Map.Entry<FilterByApriori, Set<FilePair>> filePairPerFilter : filePairsPerFilter.entrySet()) {
            sb.append(";").append(filePairPerFilter.getKey().toString());
        }
        return sb.toString();
    }

}
