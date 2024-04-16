mutable Q main(int arg) {
    Q dummy = [printTest(1) . printTest(2)];
    return nil;
}

mutable Q printTest(int i) {
    while (1 == 1)
        print(i);
    return nil;
}