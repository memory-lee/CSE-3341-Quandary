Q main(int arg) {
    mutable Ref list = makeList(arg);
    mutable Ref newList = nil;
    while (isNil(list) == 0) {
        newList = left(list) . newList;
        Ref temp = list;
        list = (Ref) right(list);
        free temp;
    }
    return newList;
}

Ref makeList(int depth) {
    if (depth == 0) return nil;
    return depth . makeList(depth - 1);
}