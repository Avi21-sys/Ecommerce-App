import hero from "../assets/hero.png";
import shirt from "../assets/images/shirt.png";
import shoes from "../assets/images/shoes.png";
import trouser from "../assets/images/trouser.png";
import watch from "../assets/images/watch.png";

const keywordMap = [
    { match: ["shoe", "sneaker"], image: shoes, label: "Footwear" },
    { match: ["shirt"], image: shirt, label: "Shirts" },
    { match: ["watch"], image: watch, label: "Watches" },
    { match: ["trouser", "pant"], image: trouser, label: "Trousers" }
];

const categoryMap = {
    shoes: { image: shoes, label: "Footwear" },
    shirt: { image: shirt, label: "Shirts" },
    watch: { image: watch, label: "Watches" },
    trouser: { image: trouser, label: "Trousers" }
};

export const heroImage = hero;

export const getProductMedia = (product = {}) => {
    if (product.image) {
        return {
            image: product.image,
            label: product.category || "Collection"
        };
    }

    const normalizedCategory = (product.category || "").toLowerCase();
    if (categoryMap[normalizedCategory]) {
        return categoryMap[normalizedCategory];
    }

    const normalizedName = (product.name || product.productName || "").toLowerCase();
    const match = keywordMap.find((entry) =>
        entry.match.some((keyword) => normalizedName.includes(keyword))
    );

    return match || { image: hero, label: "Collection" };
};
