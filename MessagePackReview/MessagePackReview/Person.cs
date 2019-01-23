using MessagePack;

namespace MessagePackReview
{
    [MessagePackObject]
    public class Person
    {
        [Key(0)]
        public string Name { get; set; }
        [Key(1)]
        public string Address { get; set; }
    }

    [MessagePackObject]
    public class PersonWithPhone
    {
        [Key(0)]
        public string Name { get; set; }
        [Key(1)]
        public string Address { get; set; }
        [Key(2)]
        public string Phone { get; set; }
    }
}
