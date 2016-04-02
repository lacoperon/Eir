from django.db import models
from PIL import ImageDraw
from PIL import Image as PILImage


class Image(models.Model):
    """
    Represents one of the available images.
    """

    # The filename of the image
    filename = models.FilePathField()

    tags = models.ManyToManyField("Tag")

    @staticmethod
    def get_relevant_images():
        # TODO!
        return Image.objects.all()

    def get_image_data(self):
        return PILImage.open(self.filename)


class Tag(models.Model):
    """
    Tag of an image
    """
    title = models.TextField(max_length=40)


class Instruction(models.Model):

    # The main image
    image = models.ForeignKey(Image)

    # When was this instruction created.
    creation_date = models.DateTimeField(auto_now_add=True)

    @staticmethod
    def latest_instruction():
        return Instruction.objects.order_by('-creation_date')[0:1].get()

    def render_instruction(self):
        """
        Renders the instruction. I.e. puts the drawings on top of the image.
        Returns a path to a png image with the overlaid drawings.
        """
        base_image = self.image.get_image_data()

        # Add all the drawings on top.
        for drawing in self.drawing_set.all():
            drawing.draw(base_image)

        # Write to temporal file.
        path = '/tmp/file.png'
        base_image.save(path, "PNG")

        return path

    def copy(self):
        """
        :return: A copy of itself
        """
        inst = Instruction()
        inst.image = self.image
        inst.save()

        # Copy all the drawings
        for drawing in self.drawing_set.all():
            # Copy the drawing
            d = drawing.copy()
            d.instruction = inst
            d.save()

        return inst


class Drawing(models.Model):

    # The shape of this drawing
    shape = models.TextField(max_length=40)

    # Location on the image
    x1 = models.IntegerField()
    y1 = models.IntegerField()

    x2 = models.IntegerField()
    y2 = models.IntegerField()

    # The instruction to which this thing belongs to.
    instruction = models.ForeignKey(Instruction)

    def draw(self, image):
        """
        Draws itself on the given image.
        """

        # Just draw a line for the moment.
        im = ImageDraw.Draw(image)
        im.line((self.x1, self.y1, self.x2, self.y2), fill=128)

    def copy(self):
        """
        :return: A copy of the drawing
        """
        drawing = Drawing()
        drawing.shape = self.shape
        drawing.x1 = self.x1
        drawing.y1 = self.y1
        drawing.x2 = self.x2
        drawing.y2 = self.y2
        return drawing

