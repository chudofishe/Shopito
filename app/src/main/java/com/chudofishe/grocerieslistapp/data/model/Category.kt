package com.chudofishe.grocerieslistapp.data.model

import com.chudofishe.grocerieslistapp.R

enum class Category(val drawable: Int, val text: Int) {
    CHILDREN (R.drawable.icons8_babys_room_100, R.string.category_children),
    BAKERY (R.drawable.icons8_bread_100, R.string.category_bakery),
    DRINKS (R.drawable.icons8_cola_100, R.string.category_drinks),
    PETS (R.drawable.icons8_corgi_100, R.string.category_pets),
    HOME (R.drawable.icons8_exterior_100, R.string.category_home),
    GROCERY (R.drawable.icons8_flour_in_paper_packaging_100, R.string.category_grocery),
    DAIRY (R.drawable. icons8_ingredients_for_cooking_100, R.string.category_dairy),
    MEDS (R.drawable.icons8_pills_100, R.string.category_meds),
    FRUITS_AND_VEGETABLES (R.drawable.icons8_salad_100, R.string.category_fruits_and_vegatables),
    MEAT (R.drawable.icons8_steak_100, R.string.category_meat),
    SNACKS (R.drawable.icons8_sweets_100, R.string.category_snacks),
    OTHER (R.drawable.icons8_task_96, R.string.category_other),
    DONE (R.drawable.icons8_task_completed_96, R.string.category_done);
}
