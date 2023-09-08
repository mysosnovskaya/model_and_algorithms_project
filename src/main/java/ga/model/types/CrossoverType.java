package ga.model.types;

public enum CrossoverType {
    PMX, // оставляет серединки, остальное меняет по цепочке
    PMX_PARTIALLY_OPTIMIZED,
    PMX_OPTIMIZED,
    _1PX, // оставляет начало, дальше сохраняет относительный порядок
    _1PX_PARTIALLY_OPTIMIZED,
    _1PX_OPTIMIZED,
    OX, // оставляет серединки, дальше сохраняет относильный порядок, начиная с отрезка "после серединки" (зацикленно)
    CX, // заполняем по цепочке, остальное - берем из другого родителя

    OurCX,

    NO,
}
