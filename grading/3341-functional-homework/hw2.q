

int isList(Q input) {
    if (isNil(input) != 0)
        return 1;
    if (isAtom(input) != 0) 
        return 0;
    return isList(right((Ref)input));
}

Ref append(Ref list1, Ref list2) {
    if (isNil(list1) != 0) 
        return list2;
    return left(list1).append((Ref)right(list1), list2);

}

Ref reverse(Ref list) {
    if (isNil(list) != 0)
        return nil;
    return append(reverse((Ref)right(list)), left(list).nil);
}



int isSorted(Ref list) {
    if (isNil(list) != 0 || isNil(right(list)) != 0)
        return 1; 

    Ref current = (Ref)left(list);
    Ref next = (Ref)left((Ref)right(list));

    if (length(current) <= length(next))
        return isSorted((Ref)right(list)); 

    return 0;
}

int length(Ref list) {
    if (isNil(list) != 0)
        return 0;
    return 1 + length((Ref)right(list));
}





int sameLength(Ref list1, Ref list2) {
    if (isNil(list1) != 0 && isNil(list2) != 0)
        return 1;
    if (isNil(list1) != 0 || isNil(list2) != 0)
        return 0; 
    return sameLength((Ref)right(list1), (Ref)right(list2));

}

