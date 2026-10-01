package org.example.backendweride;

import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.domain.model.commands.CreateVehicleCommand;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.example.backendweride.platform.location.domain.commands.CreateLocationCommand;
import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.example.backendweride.platform.location.domain.valueobjects.Coordinates;
import org.example.backendweride.platform.location.domain.valueobjects.OperatingHours;
import org.example.backendweride.platform.location.infrastructure.persistence.jpa.LocationRepository;
import org.example.backendweride.platform.plan.domain.commands.CreatePlanCommand;
import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.infrastructure.persistence.jpa.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final VehicleRepository vehicleRepository;
    private final PlanRepository planRepository;
    private final LocationRepository locationRepository;

    @Value("${app.seed.demo:true}")
    private boolean seedDemoData;

    public DatabaseSeeder(VehicleRepository vehicleRepository, PlanRepository planRepository, LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
        this.vehicleRepository = vehicleRepository;
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Demo vehicles/plans only load when app.seed.demo=true (default). Production
        // must set app.seed.demo=false so no demo data reaches real environments.
        if (!seedDemoData) return;

        // Los vehicles demo apuntan a estas locations por su id real: sin ellas el mapa no puede ubicarlos.
        List<Location> locations = locationRepository.findAll();
        if (locations.isEmpty()) {
            try {
                locations = locationRepository.saveAll(List.of(
                        location("Plaza Mayor", "Plaza Mayor, Lima Centro", -12.046374, -77.042793, 15, 8, "05:00", "23:00", List.of("WiFi", "Security", "Shelter"), "Lima", "Estación principal en el corazón histórico de Lima", "assets/locations/plaza-mayor.jpg"),
                        location("Parque Kennedy", "Av. Larco, Miraflores", -12.1205, -77.029833, 20, 12, "06:00", "22:00", List.of("WiFi", "Security", "Charging", "Shelter"), "Miraflores", "Estación en el corazón de Miraflores", "assets/locations/parque-kennedy.jpg"),
                        location("Larcomar", "Malecón de la Reserva, Miraflores", -12.131944, -77.030278, 18, 10, "07:00", "23:00", List.of("WiFi", "Security", "Charging"), "Miraflores", "Estación con vista al mar", "assets/locations/larcomar.jpg"),
                        location("Jockey Plaza", "Av. Javier Prado Este, Surco", -12.084334, -76.977074, 25, 15, "06:00", "23:30", List.of("WiFi", "Security", "Charging", "Shelter", "Lockers"), "Surco", "Estación en centro comercial principal", "assets/locations/jockey-plaza.jpg"),
                        location("San Isidro Golf", "Av. Camino Real, San Isidro", -12.097778, -77.037222, 16, 9, "06:00", "22:00", List.of("WiFi", "Security", "Shelter"), "San Isidro", "Estación en zona empresarial", "assets/locations/san-isidro-golf.jpg"),
                        location("Real Plaza Salaverry", "Av. Salaverry, Jesús María", -12.081111, -77.047222, 22, 14, "06:30", "23:00", List.of("WiFi", "Security", "Charging", "Shelter"), "Jesús María", "Estación en centro comercial", "assets/locations/real-plaza-salaverry.jpg"),
                        location("Barranco Central", "Av. Grau, Barranco", -12.146667, -77.016944, 14, 7, "07:00", "00:00", List.of("WiFi", "Security"), "Barranco", "Estación en distrito bohemio", "assets/locations/barranco-central.jpg"),
                        location("Óvalo Gutiérrez", "Av. Santa Cruz, Miraflores", -12.119444, -77.027778, 20, 11, "06:00", "22:30", List.of("WiFi", "Security", "Charging", "Shelter"), "Miraflores", "Estación en zona comercial", "assets/locations/ovalo-gutierrez.jpg"),
                        location("Pueblo Libre Centro", "Av. Brasil, Pueblo Libre", -12.075278, -77.063889, 12, 6, "06:30", "22:00", List.of("WiFi", "Security", "Shelter"), "Pueblo Libre", "Estación en zona residencial", "assets/locations/pueblo-libre.jpg"),
                        location("Universidad del Pacífico", "Av. Salaverry, Jesús María", -12.082222, -77.045278, 18, 13, "05:30", "22:30", List.of("WiFi", "Security", "Charging", "Shelter", "Student Discount"), "Jesús María", "Estación universitaria", "assets/locations/universidad-pacifico.jpg")
                ));
            } catch (DataAccessException e) {
                throw new IllegalStateException("DatabaseSeeder no pudo insertar las locations. Revisa el esquema de la base de datos.", e);
            }
        }

        if (vehicleRepository.count() == 0) {
            try {
                vehicleRepository.saveAll(List.of(
                        vehicle("Xiaomi", "M365", 2023, 82, 25, 30, 12.5, "black", "XM001", loc(locations, 0), "available", "electric_scooter", "1", 0.5, "assets/vehicles/xiaomi-m365.jpg", List.of("GPS", "Bluetooth", "Mobile App", "LED Lights"), "good", "2024-09-15T10:00:00Z", "2024-11-15T10:00:00Z", 1250.5, 4.6),
                        vehicle("Trek", "FX 3", 2022, 0, 0, 0, 11.8, "blue", "TK002", loc(locations, 1), "reserved", "bike", "1", 0.3, "assets/vehicles/trek-fx3.jpg", List.of("Lightweight", "21 Gears", "Quick Release"), "excellent", "2024-09-20T14:00:00Z", "2024-12-20T14:00:00Z", 456.2, 4.8),
                        vehicle("Segway", "Ninebot Max", 2023, 65, 30, 40, 18.7, "gray", "SG003", loc(locations, 2), "maintenance", "electric_scooter", "1", 0.6, "assets/vehicles/segway-max.jpg", List.of("GPS", "Bluetooth", "Anti-theft", "Cruise Control"), "under_repair", "2025-10-05T08:00:00Z", "2025-12-05T08:00:00Z", 892.3, 4.7),
                        vehicle("Giant", "Escape 3", 2023, 0, 0, 0, 12.9, "red", "GT004", loc(locations, 3), "available", "bike", "2", 0.35, "assets/vehicles/giant-escape3.jpg", List.of("Aluminum Frame", "24 Gears", "Disc Brakes"), "good", "2025-09-10T10:00:00Z", "2025-12-10T10:00:00Z", 678.5, 4.5),
                        vehicle("Ninebot", "ES2", 2022, 90, 25, 25, 12.5, "black", "NB005", loc(locations, 4), "available", "electric_scooter", "2", 0.45, "assets/vehicles/ninebot-es2.jpg", List.of("LED Display", "App Control", "Shock Absorption"), "excellent", "2025-09-25T14:00:00Z", "2025-11-25T14:00:00Z", 523.8, 4.9),
                        vehicle("Specialized", "Sirrus X", 2023, 0, 0, 0, 11.5, "green", "SP006", loc(locations, 5), "reserved", "bike", "2", 0.4, "assets/vehicles/specialized-sirrus.jpg", List.of("Carbon Fork", "Hydraulic Brakes", "Tubeless Ready"), "good", "2025-09-18T11:00:00Z", "2025-12-18T11:00:00Z", 345.2, 4.8),
                        vehicle("Xiaomi", "Pro 2", 2023, 75, 25, 45, 14.2, "black", "XM007", loc(locations, 6), "available", "electric_scooter", "1", 0.55, "assets/vehicles/xiaomi-pro2.jpg", List.of("GPS", "E-ABS", "Dual Brakes", "IPX4 Waterproof"), "good", "2025-09-22T09:00:00Z", "2025-11-22T09:00:00Z", 1045.7, 4.7),
                        vehicle("Segway", "E22", 2023, 88, 20, 22, 13.5, "white", "SG008", loc(locations, 7), "available", "electric_scooter", "1", 0.5, "assets/vehicles/segway-e22.jpg", List.of("Folding Design", "LED Lights", "App Control"), "excellent", "2025-09-28T10:00:00Z", "2025-11-28T10:00:00Z", 234.9, 4.6),
                        vehicle("Razor", "E300", 2022, 70, 24, 20, 21.0, "blue", "RZ009", loc(locations, 8), "available", "electric_scooter", "3", 0.4, "assets/vehicles/razor-e300.jpg", List.of("Wide Deck", "Pneumatic Tires", "Rear Suspension"), "good", "2025-09-15T13:00:00Z", "2025-11-15T13:00:00Z", 567.3, 4.4),
                        vehicle("Unagi", "Model One", 2023, 95, 32, 25, 10.9, "orange", "UN010", loc(locations, 9), "available", "electric_scooter", "3", 0.65, "assets/vehicles/unagi-one.jpg", List.of("Dual Motor", "Lightweight", "Premium Design", "Smart Display"), "excellent", "2025-10-01T08:00:00Z", "2025-12-01T08:00:00Z", 123.4, 4.9)
                ));
            } catch (DataAccessException e) {
                throw new IllegalStateException("DatabaseSeeder no pudo insertar los vehicles. Revisa el esquema de la base de datos.", e);
            }
        }

        if (planRepository.count() == 0) {
            try {
                planRepository.saveAll(List.of(
                        plan("Plan Normal", "Ideal para usuarios ocasionales que buscan una opcion economica", 3.99f, 0.6f, 5, 60, 30, 10, List.of("Acceso a scooters estandar", "10% de descuento en cada viaje", "Soporte al cliente basico", "30 minutos gratis al mes"), "#3B82F6", false),
                        plan("Plan Estudiantil", "Perfecto para estudiantes que necesitan movilidad frecuente", 5.99f, 0.4f, 10, 90, 60, 20, List.of("Acceso a scooters premium", "20% de descuento en cada viaje", "Soporte al cliente prioritario", "Viajes ilimitados los fines de semana", "60 minutos gratis al mes"), "#10B981", true),
                        plan("Plan Empresarial", "Solucion completa para profesionales y empresas", 9.99f, 0.3f, 20, 120, 120, 30, List.of("Acceso a todos los vehiculos", "30% de descuento en cada viaje", "Soporte prioritario 24/7", "Viajes ilimitados", "120 minutos gratis al mes", "Reportes mensuales", "Facturacion centralizada"), "#8B5CF6", false)
                ));
            } catch (DataAccessException e) {
                throw new IllegalStateException("DatabaseSeeder no pudo insertar los planes. Revisa el esquema de la base de datos.", e);
            }
        }
    }

    private static String loc(List<Location> locations, int index) {
        return String.valueOf(locations.get(index % locations.size()).getId());
    }

    private static Vehicle vehicle(
            String brand, String model, int year, int battery, int maxSpeed, int range,
            double weight, String color, String licensePlate, String location, String status,
            String type, String companyId, double pricePerMinute, String image, List<String> features,
            String maintenanceStatus, String lastMaintenance, String nextMaintenance,
            double totalKilometers, double rating) {
        return new Vehicle(new CreateVehicleCommand(
                brand, model, year, battery, maxSpeed, range, weight, color, licensePlate,
                location, status, type, companyId, pricePerMinute, image, features,
                maintenanceStatus, date(lastMaintenance), date(nextMaintenance), totalKilometers, rating));
    }

    private static Location location(
            String name, String address, double lat, double lng, int capacity, int availableSpots,
            String open, String close, List<String> amenities, String district, String description, String image) {
        return new Location(new CreateLocationCommand(
                name, address, new Coordinates(lat, lng), "station", capacity, availableSpots, true,
                new OperatingHours(open, close), amenities, district, description, image));
    }

    private static Plan plan(
            String name, String description, float price, float pricePerMinute, int maxTripsPerDay,
            int maxMinutesPerTrip, int freeMinutesPerMonth, int discountPercentage, List<String> benefits,
            String color, boolean popular) {
        return new Plan(new CreatePlanCommand(
                name, description, price, "USD", pricePerMinute, "monthly", 30,
                maxTripsPerDay, maxMinutesPerTrip, freeMinutesPerMonth, discountPercentage,
                benefits, color, popular, true));
    }

    private static Date date(String value) {
        return Date.from(Instant.parse(value));
    }
}
