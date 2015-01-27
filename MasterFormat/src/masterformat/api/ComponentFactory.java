package masterformat.api;

import Concrete.Concrete;
import masonry.Masonry;

public abstract class ComponentFactory {
    
    //manufacture masonry from the list
    public abstract Masonry getMasonry(String masonryType);
    
    public abstract Concrete getConcrete(String concreteType);
}
