package br.edu.utfpr.recominer.controller;

import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.model.FileIssueMetrics;
import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import br.edu.utfpr.recominer.model.matrix.EntityMatrixNode;
import br.edu.utfpr.recominer.model.metric.EntityMetric;
import br.edu.utfpr.recominer.model.metric.EntityMetricNode;
import br.edu.utfpr.recominer.util.JsfUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author douglas
 */
@Named
@RequestScoped
public class BichoMetricViewBean implements Serializable {

    private final String FOR_DELETE = "metricForDelete";
    private final String LIST = "metricList";

    @Inject
    private GenericDao dao;

    /**
     * Creates a new instance of GitNetView
     */
    public BichoMetricViewBean() {
    }

    public void delete() {
        try {
            EntityMetric forDelete = (EntityMetric) JsfUtil.getObjectFromSession(FOR_DELETE);
            dao.remove(forDelete);
            reloadList();
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
        removeFromSession();
    }

    public void deleteAll() {
        for (EntityMetric metric : getMetrics()) {
            dao.remove(metric);
        }
        reloadList();
    }

    public void removeFromSession() {
        JsfUtil.removeAttributeFromSession(FOR_DELETE);
    }

    public void addForDeleteInSession(EntityMetric forDelete) {
        JsfUtil.addAttributeInSession(FOR_DELETE, forDelete);
    }

    public void reloadList() {
        dao.clearCache(true);
        List<EntityMetric> metrics = dao.executeNamedQuery("Metric.findAllTheLatest", EntityMetric.class);
        JsfUtil.addAttributeInSession(LIST, metrics);
    }

    public List<EntityMetric> getMetrics() {
        List<EntityMetric> metrics = (List<EntityMetric>) JsfUtil.getObjectFromSession(LIST);
        if (metrics == null) {
            reloadList();
            return getMetrics();
        }
        return metrics;
    }

    public void downloadAllCSV() {
        try {
            ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipBytes);
            zos.setLevel(9);

            for (EntityMetric metric : getMetrics()) {
                System.out.println("Metric " + metric + " tem nodes: " + metric.getNodes().size());

                String fileName = generateFileName(metric) + ".csv";

                StringBuilder csv = new StringBuilder();

                for (EntityMetricNode node : metric.getNodes()) {
                    csv.append(node).append("\r\n");
                }

                ZipEntry ze = new ZipEntry(fileName.replaceAll("/", "-"));
                zos.putNextEntry(ze);
                zos.write(csv.toString().getBytes());
                zos.closeEntry();

            }

            zos.close();
            download("All.zip", "application/zip", zipBytes.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    public void downloadAllCSVOfOneVersion(String version) {
        try {
            ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipBytes);
            zos.setLevel(9);

            for (EntityMetric metric : getMetrics()) {
                if (!metric.toString().matches("^" + version + "\\s.*$")
                        && !metric.toString().equals(version)) {
                    continue;
                }
                System.out.println("Metric " + metric + " tem nodes: " + metric.getNodes().size());

                String fileName = generateFileName(metric) + ".csv";

                StringBuilder csv = new StringBuilder();
                for (EntityMetricNode node : metric.getNodes()) {
                    csv.append(node).append("\r\n");
                }

                ZipEntry ze = new ZipEntry(fileName.replaceAll("/", "-"));
                zos.putNextEntry(ze);
                zos.write(csv.toString().getBytes());
                zos.closeEntry();

            }

            zos.close();
            download(version + ".zip", "application/zip", zipBytes.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    public void downloadAllCSVNotEmptyInFolderCustom() {
        try {
            ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipBytes);
            zos.setLevel(9);
            Set<String> files = new HashSet<>();

            // set of all files in zip
            Set<String> toDownload = new HashSet<>();

            for (EntityMetric metric : getMetrics()) {
                if (metric.getNodes().size() == 1) {
                    continue; // skip empty files (i.e. having only the header)
                }
                String path = createPath(metric);

                toDownload.add(path);
            }

            for (EntityMetric metric : getMetrics()) {
                if (metric.getNodes().size() <= 1) {
                    // skip empty files (i.e. having only the header)
                    continue;
                } else {
                    metric.getNodes().set(0, new EntityMetricNode(FileIssueMetrics.HEADER));
                }

                String path = createPath(metric);
//                if (!hasTrainAndTest(metric, toDownload)) {
//                    System.out.println("No train or test for " + path);
//                    // skip when has no train and test files
//                    continue;
//                }

                System.out.println("Metric " + path + " tem nodes: " + metric.getNodes().size());

                if (!files.contains(path)) {
                    StringBuilder csv = new StringBuilder();
                    for (EntityMetricNode node : metric.getNodes()) {
                        String line = node.toString();
                        if (line.endsWith(";")) { // prevents error weka/r, remove last ;
                            csv.append(line.replaceAll("NaN", "0.0").substring(0, line.length() - 1));
                        } else {
                            csv.append(line.replaceAll("NaN", "0.0"));
                        }
                        csv.append("\r\n");
                    }
                    ZipEntry ze = new ZipEntry(path);
                    zos.putNextEntry(ze);
                    zos.write(csv.toString().getBytes());
                    zos.closeEntry();
                    files.add(path);
                }
            }
            zos.close();
            final String project = getMetrics().get(0).getParams().get("project").toString();
            download(StringUtils.capitalize(project) + " Metrics.zip", "application/zip", zipBytes.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    public void downloadAllCSVNotEmptyInFolderPerRankOnlyTrainFile() {
        try {
            ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipBytes);
            zos.setLevel(9);
            Set<String> files = new HashSet<>();

            dao.clearCache(true);
            List<EntityMatrix> matrices = dao.executeNamedQuery("Matrix.findAllTheLatest", EntityMatrix.class);

            List<String> topFiles = new ArrayList<>();
            for (EntityMatrix matrix : matrices) {
                final Object filename = matrix.getParams().get("filename");
                if (filename != null
                        && filename.equals("top 10 percent files in last 500 fixed issues")) {
                    final Iterator<EntityMatrixNode> iterator = matrix.getNodes().iterator();
                    iterator.next();
                    for (; iterator.hasNext();) {
                        final String line = iterator.next().getLine();
                        topFiles.add(line.substring(0, line.indexOf(';')));
                    }
                    break;
                }
            }

            String project = getProject(getMetrics().get(0));

            int rankTopFile = 1;
            int rankPairFile = 1;
            for (String topFile : topFiles) {
                int countLines = 0;
                for (EntityMetric metric : getMetrics()) {
                    final String firstLine = metric.getNodes().get(1).getLine();
                    String matrixFile1 = firstLine.substring(0, firstLine.indexOf(';'));
                    if (matrixFile1.equals(topFile)) {
                        StringBuilder csv = new StringBuilder();
                        for (EntityMetricNode node : metric.getNodes()) {
                            String line = node.toString();
                            if (line.endsWith(";")) { // prevents error weka/r, remove last ;
                                csv.append(line.replaceAll("NaN", "0.0").substring(0, line.length() - 1));
                            } else {
                                csv.append(line.replaceAll("NaN", "0.0"));
                            }
                            csv.append(line).append("\r\n");
                            countLines++;

                        }
                        String path = project + "/" + rankTopFile + "/" + rankPairFile++ + ".csv";
                        System.out.println("Metric " + path + " tem nodes: " + countLines);

                        ZipEntry ze = new ZipEntry(path);
                        zos.putNextEntry(ze);
                        zos.write(csv.toString().getBytes());
                        zos.closeEntry();
                        files.add(path);
                    }
                }
                rankTopFile++;
            }
            zos.close();
            download(StringUtils.capitalize(project) + " Metrics.zip", "application/zip", zipBytes.toByteArray());

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    private boolean hasTrainAndTest(EntityMetric metric, Set<String> toDownload) {
        final String trainOrTest = metric.getParams().get("additionalFilename").toString();
        if ("train".equals(trainOrTest)) {
            final String testPath = createPath(metric, "test");
            return toDownload.contains(testPath);
        } else {
            final String trainPath = createPath(metric, "train");
            return toDownload.contains(trainPath);
        }
    }

    public String createPathOneFilePerRank(EntityMetric metric) {
        final String rank = metric.getParams().get("rank").toString();
        final String path = getProject(metric) + "/" + rank + ".csv";
        return path;
    }

    public String getProject(EntityMetric metric) {
        return metric.getParams().get("project").toString();
    }

    private String createPath(EntityMetric metric) {
        final Object additionalFilename = metric.getParams().get("additionalFilename");
        final String trainOrTest = additionalFilename.toString();
        return createPath(metric, trainOrTest);
    }

    public String createPath(EntityMetric metric, final String trainOrTest) {
        final String project = metric.getParams().get("project").toString();
        final String version;
        final boolean existsVersionInAnalysis = metric.getParams().get("versionInAnalysis") != null
                && StringUtils.isNotBlank(metric.getParams().get("versionInAnalysis").toString());
        final boolean existsIndexInAnalysis = metric.getParams().get("indexInAnalysis") != null
                && StringUtils.isNotBlank(metric.getParams().get("indexInAnalysis").toString());

        if (existsVersionInAnalysis) {
            version = " " + metric.getParams().get("versionInAnalysis").toString();
        } else if (existsIndexInAnalysis) {
            version = " " + metric.getParams().get("indexInAnalysis").toString();
        } else {
            version = "";
        }

        final String projectVersion = project + version;
        final String aprioriFilter = metric.getParams().get("aprioriFilter").toString();
        final String rank = metric.getParams().get("rank").toString();
        final String path = aprioriFilter + "/" + projectVersion + "/" + rank + "/" + trainOrTest + ".csv";
        return path;
    }

    public void downloadAllCSVNotEmptyInFolder() {
        try {
            ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipBytes);
            zos.setLevel(9);
            String project = "All";
            Set<String> files = new HashSet<>();
            Set<String> downloaded = new HashSet<>();
            for (EntityMetric metric : getMetrics()) {
                final String metricName = metric.toString();
                // download lasts metrics
                if (downloaded.contains(metricName)) {
                    continue;
                } else {
                    downloaded.add(metricName);
                }

                System.out.println("Metric " + metricName + " tem nodes: " + metric.getNodes().size());

                if (metric.getNodes().size() == 1) {
                    continue;
                }
                String fileName = generateFileName(metric);

                if (!files.contains(fileName)) {
                    StringBuilder csv = new StringBuilder();
                    for (EntityMetricNode node : metric.getNodes()) {
                        String line = node.toString();
                        if (line.endsWith(";")) { // prevents error weka/r, remove last ;
                            csv.append(line.replaceAll("NaN", "0.0").substring(0, line.length() - 1));
                        } else {
                            csv.append(line.replaceAll("NaN", "0.0"));
                        }
                        csv.append("\r\n");
                    }
                    ZipEntry ze = new ZipEntry(fileName);
                    zos.putNextEntry(ze);
                    zos.write(csv.toString().getBytes());
                    zos.closeEntry();
                    files.add(fileName);
                }
            }
            zos.close();
            download(project + ".zip", "application/zip", zipBytes.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    public void downloadCSV(EntityMetric metric) {
        try {
            System.out.println("Metric tem nodes: " + metric.getNodes().size());

            String fileName = generateFileName(metric) + ".csv";

            StringBuilder csv = new StringBuilder();

            for (EntityMetricNode node : metric.getNodes()) {
                csv.append(node).append("\r\n");
            }

            download(fileName, "text/csv", csv.toString().getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    public void downloadLOG(EntityMetric metric) {
        try {
            String fileName = generateFileName(metric) + ".log";
            download(fileName, "text/plain", metric.getLog().getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    public void downloadParams(EntityMetric metric) {
        try {
            String fileName = generateFileName(metric) + ".txt";

            StringBuilder params = new StringBuilder();

            for (Map.Entry<Object, Object> entrySet : metric.getParams().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                params.append(key).append("=").append(value).append("\r\n");
            }

            download(fileName, "text/plain", params.toString().getBytes());

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.toString());
        }
    }

    public void download(String filename, String contentType, byte[] content) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        ec.setResponseContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
        ec.setResponseContentLength(content.length); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
        try (OutputStream output = ec.getResponseOutputStream()) {
            output.write(content);
            output.flush();
            fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
        }
    }

    private String generateFileName(EntityMetric metric) {
        return metric.toString();
    }
}
