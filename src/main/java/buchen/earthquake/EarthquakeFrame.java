package buchen.earthquake;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import buchen.earthquake.json.Feature;
import buchen.earthquake.json.FeatureCollection;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class EarthquakeFrame extends JFrame {

    public JList<String> earthquakeList = new JList<>();
    public JRadioButton oneHour = new JRadioButton("One hour");
    public JRadioButton significant30 = new JRadioButton("30 days");
    private FeatureCollection curr;

    public EarthquakeFrame() {
        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioButtonPanel.add(oneHour);
        radioButtonPanel.add(significant30);

        ButtonGroup group = new ButtonGroup();
        group.add(oneHour);
        group.add(significant30);

        add(radioButtonPanel, BorderLayout.PAGE_START);
        add(earthquakeList, BorderLayout.CENTER);

        add(new JScrollPane(earthquakeList), BorderLayout.CENTER);

        EarthquakeService service = new EarthquakeServiceFactory().getService();

        oneHour.addActionListener(e -> {
            if (oneHour.isSelected()) {
                Disposable disposable = service.oneHour()
                        // tells Rx to request the data on a background Thread
                        .subscribeOn(Schedulers.io())
                        // tells Rx to handle the response on Swing's main Thread
                        .observeOn(SwingSchedulers.edt())
                        //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                        .subscribe(
                                this::handleResponse,
                                Throwable::printStackTrace);
            }
        });

        significant30.addActionListener(e -> {
            if (significant30.isSelected()) {
                Disposable disposable2 = service.significant30()
                        // tells Rx to request the data on a background Thread
                        .subscribeOn(Schedulers.io())
                        // tells Rx to handle the response on Swing's main Thread
                        .observeOn(SwingSchedulers.edt())
                        //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                        .subscribe(
                                this::handleResponse,
                                Throwable::printStackTrace);
            }
        });

        ListSelectionModel listSelectionModel = earthquakeList.getSelectionModel();
        listSelectionModel.addListSelectionListener(e -> {
            Feature feature = curr.features[earthquakeList.getSelectedIndex()];
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(
                            new URI("https://www.google.com/maps/search/?api=1&query=" + feature.geometry.coordinates[1] + "," + feature.geometry.coordinates[0]));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    private void handleResponse(FeatureCollection response) {
        curr = response;
        String[] listData = new String[response.features.length];
        for (int i = 0; i < response.features.length; i++) {
            Feature feature = response.features[i];
            listData[i] = feature.properties.mag + " " + feature.properties.place + " "
                    + feature.geometry.coordinates[1] + " " + feature.geometry.coordinates[0];
        }
        earthquakeList.setListData(listData);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EarthquakeFrame().setVisible(true));
    }
}