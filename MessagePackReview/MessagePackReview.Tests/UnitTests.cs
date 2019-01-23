using MessagePack;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace MessagePackReview.Tests
{
    [TestClass]
    public class UnitTests
    {
        [TestMethod]
        public void TestBackwardsCompatibility1()
        {
            // ARRANGE
            var person = new Person { Name = "John Smith", Address = "Missouri" };
            var bytes = LZ4MessagePackSerializer.Serialize(person);

            // ACT
            var personWithPhone = LZ4MessagePackSerializer.Deserialize<PersonWithPhone>(bytes);

            // ASSERT
            Assert.AreEqual(person.Name, personWithPhone.Name);
            Assert.AreEqual(person.Address, personWithPhone.Address);
            Assert.AreEqual(null, personWithPhone.Phone);
        }

        [TestMethod]
        public void TestBackwardsCompatibility2()
        {
            // ARRANGE
            var personWithPhone = new PersonWithPhone { Name = "John Smith", Address = "Missouri", Phone = "00723121211" };
            var bytes = LZ4MessagePackSerializer.Serialize(personWithPhone);

            // ACT
            var person = LZ4MessagePackSerializer.Deserialize<Person>(bytes);

            // ASSERT
            Assert.AreEqual(personWithPhone.Name, person.Name);
            Assert.AreEqual(personWithPhone.Address, person.Address);
        }
    }
}
