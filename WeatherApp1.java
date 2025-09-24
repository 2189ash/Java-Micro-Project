import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp1 extends JFrame {
    private static final String API_KEY = "70c80a2fbbda76031e9f084ae41c0e6d"; // Replace with your OpenWeatherMap API key
    private JTextField cityField;
    private JTextArea resultArea;

    public WeatherApp1() {
        setTitle("Weather Application");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel();
        cityField = new JTextField(20);
        JButton getWeatherButton = new JButton("Get Weather");

        inputPanel.add(new JLabel("Enter City:"));
        inputPanel.add(cityField);
        inputPanel.add(getWeatherButton);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button action
        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText();
                getWeather(city);
            }
        });
    }

    private void getWeather(String city) {
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + API_KEY + "&units=metric"; // Metric for Celsius

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Parse the JSON response
                parseWeatherData(response.toString());
            } else {
                resultArea.setText("City not found. Please check the name and try again.");
            }
        } catch (IOException e) {
            resultArea.setText("Error fetching weather data: " + e.getMessage());
        }
    }

    private void parseWeatherData(String jsonResponse) {
        try {
            String cityName = jsonResponse.split("\"name\":\"")[1].split("\"")[0];
            String country = jsonResponse.split("\"country\":\"")[1].split("\"")[0];
            String temperature = jsonResponse.split("\"temp\":")[1].split(",")[0];
            String weatherDescription = jsonResponse.split("\"description\":\"")[1].split("\"")[0];
            String result = "Weather in " + cityName + ", " + country + ":\n" +
                            "Temperature: " + temperature + "°C\n" +
                            "Description: " + weatherDescription;
            resultArea.setText(result);
        } catch (ArrayIndexOutOfBoundsException e) {
            resultArea.setText("Error parsing weather data.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherApp1 app = new WeatherApp1();
            app.setVisible(true);
        });
    }
}