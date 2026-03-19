package org.example.salon_project.frontend.seed;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.BeautyService;
import org.example.salon_project.domain.BookingSlot;
import org.example.salon_project.domain.Category;
import org.example.salon_project.domain.Client;
import org.example.salon_project.domain.Drink;
import org.example.salon_project.domain.LocalizedTextEmbeddable;
import org.example.salon_project.domain.Master;
import org.example.salon_project.domain.Product;
import org.example.salon_project.domain.SalonBooking;
import org.example.salon_project.domain.Trend;
import org.example.salon_project.repository.BeautyServiceRepository;
import org.example.salon_project.repository.BookingSlotRepository;
import org.example.salon_project.repository.CategoryRepository;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.repository.DrinkRepository;
import org.example.salon_project.repository.MasterRepository;
import org.example.salon_project.repository.ProductRepository;
import org.example.salon_project.repository.SalonBookingRepository;
import org.example.salon_project.repository.TrendRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FrontendDataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final MasterRepository masterRepository;
    private final BookingSlotRepository bookingSlotRepository;
    private final ProductRepository productRepository;
    private final DrinkRepository drinkRepository;
    private final TrendRepository trendRepository;
    private final SalonBookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedAdminAccount();
        seedCategories();
        seedServices();
        seedMasters();
        seedBookingSlots();
        seedProducts();
        seedDrinks();
        seedTrends();
        seedBookings();
    }

    private void seedAdminAccount() {
        if (clientRepository.findByExternalId("account-admin").isPresent()
                || clientRepository.findByEmailIgnoreCase("admin@tintel.beauty").isPresent()) {
            return;
        }

        clientRepository.save(Client.builder()
                .externalId("account-admin")
                .firstName("Salon")
                .lastName("Admin")
                .phone("+49 30 555 1000")
                .email("admin@tintel.beauty")
                .passwordHash(passwordEncoder.encode("golden-admin"))
                .role("admin")
                .language("UA")
                .build());
    }

    private void seedCategories() {
        ensureCategory("cat-hair", 0, "hair", "✂",
                text("Волосся", "Haare", "Hair"),
                text("Стрижки, фарбування та догляд", "Cuts, Colorationen und Pflege", "Cuts, colour and care rituals"));
        ensureCategory("cat-nails", 1, "nails", "◌",
                text("Нігті", "Nails", "Nails"),
                text("Манікюр, педикюр та дизайн", "Maniküre, Pediküre und Design", "Manicure, pedicure and nail design"));
        ensureCategory("cat-makeup", 2, "makeup", "✦",
                text("Макіяж", "Make-up", "Makeup"),
                text("Образи для дня, вечора та подій", "Looks für Tag, Abend und Events", "Looks for day, evening and events"));
        ensureCategory("cat-styling", 3, "styling", "∞",
                text("Стайлінг", "Styling", "Styling"),
                text("Укладання, текстура та фініш", "Styling, Textur und Finish", "Styling, texture and finish"));
    }

    private void seedServices() {
        ensureService("srv-signature-cut", 0, "cat-hair", text("Фірмова стрижка", "Signature Cut", "Signature Cut"), 60, 65);
        ensureService("srv-gloss-color", 1, "cat-hair", text("Тонування та блиск", "Glossing und Shine", "Gloss and Shine"), 105, 110);
        ensureService("srv-repair-ritual", 2, "cat-hair", text("Відновлюючий ритуал", "Repair Ritual", "Repair Ritual"), 75, 85);
        ensureService("srv-lux-manicure", 3, "cat-nails", text("Люкс манікюр", "Luxus Maniküre", "Luxury Manicure"), 60, 48);
        ensureService("srv-gel-sculpt", 4, "cat-nails", text("Гелеве моделювання", "Gel Modellage", "Gel Sculpt"), 90, 72);
        ensureService("srv-event-glam", 5, "cat-makeup", text("Вечірній glam", "Abend Glam", "Evening Glam"), 70, 92);
        ensureService("srv-bridal-preview", 6, "cat-makeup", text("Весільний тест-образ", "Braut Probetermin", "Bridal Preview"), 95, 130);
        ensureService("srv-soft-waves", 7, "cat-styling", text("М’які хвилі", "Soft Waves", "Soft Waves"), 45, 55);
        ensureService("srv-red-carpet", 8, "cat-styling", text("Red carpet укладка", "Red Carpet Styling", "Red Carpet Styling"), 80, 95);
    }

    private void seedMasters() {
        ensureMaster("master-anna", 0, "Anna Kovalenko", text("Топ-стиліст", "Top Stylistin", "Top Stylist"), "AK", "8+ years", List.of("cat-hair", "cat-styling"));
        ensureMaster("master-maria", 1, "Maria Petrenko", text("Колорист", "Coloristin", "Colour Specialist"), "MP", "6+ years", List.of("cat-hair"));
        ensureMaster("master-olena", 2, "Olena Shevchenko", text("Майстер нігтьового сервісу", "Nail Artist", "Nail Artist"), "OS", "7+ years", List.of("cat-nails"));
        ensureMaster("master-iryna", 3, "Iryna Bondar", text("Make-up artist", "Make-up Artist", "Makeup Artist"), "IB", "5+ years", List.of("cat-makeup", "cat-styling"));
    }

    private void seedBookingSlots() {
        ensureBookingSlot("slot-0900", 0, "morning", "09:00");
        ensureBookingSlot("slot-0930", 1, "morning", "09:30");
        ensureBookingSlot("slot-1000", 2, "morning", "10:00");
        ensureBookingSlot("slot-1030", 3, "morning", "10:30");
        ensureBookingSlot("slot-1130", 4, "morning", "11:30");
        ensureBookingSlot("slot-1200", 5, "afternoon", "12:00");
        ensureBookingSlot("slot-1230", 6, "afternoon", "12:30");
        ensureBookingSlot("slot-1330", 7, "afternoon", "13:30");
        ensureBookingSlot("slot-1430", 8, "afternoon", "14:30");
        ensureBookingSlot("slot-1600", 9, "evening", "16:00");
        ensureBookingSlot("slot-1700", 10, "evening", "17:00");
        ensureBookingSlot("slot-1800", 11, "evening", "18:00");
    }

    private void seedProducts() {
        ensureProduct("prod-gold-shampoo", 0, text("Шампунь Gold Recovery", "Gold Recovery Shampoo", "Gold Recovery Shampoo"), "Kérastase", 29, "◍");
        ensureProduct("prod-velvet-mask", 1, text("Маска Velvet Repair", "Velvet Repair Maske", "Velvet Repair Mask"), "Oribe", 46, "◎");
        ensureProduct("prod-argan-serum", 2, text("Argan Shine serum", "Argan Shine Serum", "Argan Shine Serum"), "Moroccanoil", 38, "◇");
        ensureProduct("prod-volume-mist", 3, text("Volume pearl mist", "Volume Pearl Mist", "Volume Pearl Mist"), "Aveda", 24, "✧");
    }

    private void seedDrinks() {
        ensureDrink("drink-espresso", 0, text("Еспресо", "Espresso", "Espresso"), "☕");
        ensureDrink("drink-cappuccino", 1, text("Капучино", "Cappuccino", "Cappuccino"), "◔");
        ensureDrink("drink-jasmine", 2, text("Жасминовий чай", "Jasmin Tee", "Jasmine Tea"), "❀");
        ensureDrink("drink-lemon-water", 3, text("Лимонна вода", "Zitronenwasser", "Lemon Water"), "◐");
    }

    private void seedTrends() {
        ensureTrend("trend-balayage", 0,
                text("Sunlit balayage", "Sunlit Balayage", "Sunlit Balayage"),
                text("М’який перелив кольору з теплим перлинним сяйвом.", "Weicher Farbfluss mit warmem Perlschimmer.", "Soft colour melt with a warm pearly finish."),
                "linear-gradient(135deg, #c59b57 0%, #f0d7a1 45%, #d5b277 100%)",
                "✦");
        ensureTrend("trend-sculpted-bob", 1,
                text("Sculpted bob", "Sculpted Bob", "Sculpted Bob"),
                text("Чисті лінії та дзеркальний блиск.", "Klare Linien und spiegelnder Glanz.", "Clean lines with a mirror-like shine."),
                "linear-gradient(135deg, #697680 0%, #c9d2d8 55%, #8e9ca6 100%)",
                "✂");
        ensureTrend("trend-luxe-texture", 2,
                text("Luxe texture", "Luxe Texture", "Luxe Texture"),
                text("Повітряний об’єм, текстура та гнучкий фініш.", "Luftiges Volumen, Textur und flexibles Finish.", "Airy volume, texture and a flexible finish."),
                "linear-gradient(135deg, #8f6b56 0%, #e5c5ac 50%, #bd8e72 100%)",
                "∞");
    }

    private void seedBookings() {
        ensureBooking("booking-seed-1", 0, "cat-hair", "srv-gloss-color", "master-maria",
                "Sofia", "Miller", "+49 176 555 1122", "sofia@example.com",
                LocalDate.parse("2026-03-14"), LocalTime.parse("12:30"), "confirmed",
                "First visit, warm blonde reference.", OffsetDateTime.parse("2026-03-08T12:00:00.000Z"));
        ensureBooking("booking-seed-2", 1, "cat-makeup", "srv-event-glam", "master-iryna",
                "Emma", "Roth", "+49 171 555 7788", "emma@example.com",
                LocalDate.parse("2026-03-15"), LocalTime.parse("16:00"), "new",
                "Evening gala at 20:00.", OffsetDateTime.parse("2026-03-10T16:45:00.000Z"));
    }

    private void ensureCategory(String externalId, int sortOrder, String slug, String icon, LocalizedTextEmbeddable title, LocalizedTextEmbeddable description) {
        if (categoryRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        categoryRepository.save(Category.builder()
                .externalId(externalId)
                .slug(slug)
                .icon(icon)
                .name(title.getGb())
                .description(description.getGb())
                .title(title)
                .localizedDescription(description)
                .sortOrder(sortOrder)
                .build());
    }

    private void ensureService(String externalId, int sortOrder, String categoryExternalId, LocalizedTextEmbeddable title, int durationMinutes, int price) {
        if (beautyServiceRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        Category category = categoryRepository.findByExternalId(categoryExternalId).orElseThrow();
        beautyServiceRepository.save(BeautyService.builder()
                .externalId(externalId)
                .category(category)
                .name(title.getGb())
                .title(title)
                .durationMinutes(durationMinutes)
                .price(BigDecimal.valueOf(price))
                .active(true)
                .sortOrder(sortOrder)
                .build());
    }

    private void ensureMaster(String externalId, int sortOrder, String displayName, LocalizedTextEmbeddable role, String initials, String experienceLabel, List<String> specialties) {
        if (masterRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        String[] name = splitName(displayName);
        LinkedHashSet<Category> specialtyCategories = new LinkedHashSet<>(specialties.stream()
                .map(categoryExternalId -> categoryRepository.findByExternalId(categoryExternalId).orElseThrow())
                .toList());
        masterRepository.save(Master.builder()
                .externalId(externalId)
                .firstName(name[0])
                .lastName(name[1])
                .displayName(displayName)
                .role(role)
                .initials(initials)
                .experienceLabel(experienceLabel)
                .active(true)
                .specialtyCategories(specialtyCategories)
                .sortOrder(sortOrder)
                .build());
    }

    private void ensureBookingSlot(String externalId, int sortOrder, String period, String time) {
        if (bookingSlotRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        bookingSlotRepository.save(BookingSlot.builder()
                .externalId(externalId)
                .period(period)
                .time(LocalTime.parse(time))
                .sortOrder(sortOrder)
                .build());
    }

    private void ensureProduct(String externalId, int sortOrder, LocalizedTextEmbeddable title, String brand, int price, String icon) {
        if (productRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        productRepository.save(Product.builder()
                .externalId(externalId)
                .name(title.getGb())
                .title(title)
                .brand(brand)
                .price(BigDecimal.valueOf(price))
                .icon(icon)
                .stock(100)
                .sortOrder(sortOrder)
                .build());
    }

    private void ensureDrink(String externalId, int sortOrder, LocalizedTextEmbeddable title, String icon) {
        if (drinkRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        drinkRepository.save(Drink.builder()
                .externalId(externalId)
                .name(title.getGb())
                .title(title)
                .icon(icon)
                .available(true)
                .price(BigDecimal.ZERO)
                .sortOrder(sortOrder)
                .build());
    }

    private void ensureTrend(String externalId, int sortOrder, LocalizedTextEmbeddable title, LocalizedTextEmbeddable description, String gradient, String emoji) {
        if (trendRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        trendRepository.save(Trend.builder()
                .externalId(externalId)
                .title(title)
                .description(description)
                .gradient(gradient)
                .emoji(emoji)
                .sortOrder(sortOrder)
                .build());
    }

    private void ensureBooking(String externalId, int sortOrder, String categoryId, String serviceId, String masterId,
                               String firstName, String lastName, String phone, String email,
                               LocalDate date, LocalTime time, String status, String note, OffsetDateTime createdAt) {
        if (bookingRepository.findByExternalId(externalId).isPresent()) {
            return;
        }
        bookingRepository.save(SalonBooking.builder()
                .externalId(externalId)
                .category(categoryRepository.findByExternalId(categoryId).orElseThrow())
                .service(beautyServiceRepository.findByExternalId(serviceId).orElseThrow())
                .master(masterRepository.findByExternalId(masterId).orElseThrow())
                .customerFirstName(firstName)
                .customerLastName(lastName)
                .customerPhone(phone)
                .customerEmail(email)
                .bookingDate(date)
                .bookingTime(time)
                .status(status)
                .note(note)
                .createdAt(createdAt)
                .sortOrder(sortOrder)
                .build());
    }

    private LocalizedTextEmbeddable text(String ua, String de, String gb) {
        return LocalizedTextEmbeddable.builder()
                .ua(ua)
                .de(de)
                .gb(gb)
                .build();
    }

    private String[] splitName(String displayName) {
        int idx = displayName.indexOf(' ');
        if (idx < 0) {
            return new String[] { displayName, "" };
        }
        return new String[] { displayName.substring(0, idx), displayName.substring(idx + 1).trim() };
    }
}
