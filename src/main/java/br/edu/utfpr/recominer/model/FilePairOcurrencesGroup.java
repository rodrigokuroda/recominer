package br.edu.utfpr.recominer.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePairOcurrencesGroup {

    private final Map<FilterFilePairByReleaseOcurrence, AtomicInteger> groupingCount;

    public FilePairOcurrencesGroup() {
        this.groupingCount = new LinkedHashMap<>();
    }

    public FilePairOcurrencesGroup(Collection<FilterFilePairByReleaseOcurrence> filterList) {
        this.groupingCount = new LinkedHashMap<>();
        for (FilterFilePairByReleaseOcurrence grouping : filterList) {
            groupingCount.put(grouping, new AtomicInteger());
        }
    }

    public void addFilterFilePair(FilterFilePairByReleaseOcurrence filter) {
        groupingCount.put(filter, new AtomicInteger());
    }

    public int getQuantity(FilterFilePairByReleaseOcurrence filter) {
        if (groupingCount.get(filter) != null) {
            return groupingCount.get(filter).get();
        }
        throw new IllegalArgumentException("Filter for group does not exists.");
    }

    private void increment(FilterFilePairByReleaseOcurrence filter) {
        groupingCount.get(filter).incrementAndGet();
    }

    public void groupFilePairs(Collection<FilePairReleasesOccurenceCounter> counters, int minOccurrencesInAnyVersion) {
        for (FilePairReleasesOccurenceCounter counter : counters) {
            if (counter.hasAtLeastOccurrencesInOneVersion(minOccurrencesInAnyVersion)) {
                for (FilterFilePairByReleaseOcurrence group : groupingCount.keySet()) {
                    if (group.fits(counter)) {
                        increment(group);
                    }
                }
            }
        }
    }

    /**
     * Count the quantity of file pair of each filter (group/classification)
     * specified in constructor.
     *
     * @param allVersions
     * @param counters The counters of each file pair to classify based on
     * filters.
     * @param minOccurrencesInOneVersion Minimum occurrences of file pair in
     * version to considers in a sequence of versions.
     */
    public void groupFilePairs(List<Version> allVersions,
            Collection<FilePairReleasesOccurenceCounter> counters, int minOccurrencesInOneVersion) {
        for (FilePairReleasesOccurenceCounter counter : counters) {
            // optimization: check if file pair was changed in some version with the minimum occurrences
            if (counter.hasAtLeastOccurrencesInOneVersion(minOccurrencesInOneVersion)) {
                // checking which group the file pair is, based on its number of change sequence through versions
                for (FilterFilePairByReleaseOcurrence group : groupingCount.keySet()) {
                    if (group.fitsVersionSequenceOccurrences(counter, minOccurrencesInOneVersion)) {
                        increment(group);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<FilterFilePairByReleaseOcurrence, AtomicInteger> entrySet : groupingCount.entrySet()) {
            AtomicInteger value = entrySet.getValue();
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(value.get());
        }

        return sb.toString();
    }

    public String getDynamicHeader() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<FilterFilePairByReleaseOcurrence, AtomicInteger> entrySet : groupingCount.entrySet()) {
            FilterFilePairByReleaseOcurrence key = entrySet.getKey();

            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(key);
        }
        return sb.toString();
    }

}
