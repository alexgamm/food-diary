package fooddiary.service;

import fooddiary.model.Food;
import fooddiary.fatsecret.FatSecretSearchClient;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FoodSearchService {
    private final FatSecretSearchClient fatSecretSearchClient;

    public Optional<Food> search(@NotNull String name, @Nullable Float kcal) {
        List<Food> foods = fatSecretSearchClient.search(name);
        if (kcal == null) {
            return foods.stream().findFirst();
        } else {
            return foods.stream()
                    .min(Comparator.comparing(food -> Math.abs(food.getKcal() - kcal)))
                    .map(food -> new Food(food.getName(), kcal, food.getProtein(), food.getFat(), food.getCarbs()));
        }
    }
}
