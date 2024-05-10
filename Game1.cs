using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;
using System.Threading;

namespace monogame1
{
    public enum Direction {  Left, Right, Up , Down};

    public class Game1 : Game
    {
        private GraphicsDeviceManager _graphics;
        private SpriteBatch _spriteBatch;

        private AnimatedSprite LinkLeft;
        private AnimatedSprite LinkRight;
        private AnimatedSprite LinkUp;
        private AnimatedSprite LinkDown;
        private Texture2D BackGround;
        private Vector2 spritePos;
        private float spriteSpeed;
        private Direction direction;//left 0 up 1 right 2 down 3
        public Game1()
        {
            _graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
            IsMouseVisible = true;
            _graphics.PreferredBackBufferWidth = 800;
            _graphics.PreferredBackBufferHeight = 600;
            _graphics.ApplyChanges();
        }

        protected override void Initialize()
        {
            // TODO: Add your initialization logic here
            spritePos = new Vector2(_graphics.PreferredBackBufferWidth / 2,_graphics.PreferredBackBufferHeight / 2);
            spriteSpeed = 100f;
            direction = Direction.Down;
            base.Initialize();
        }
        protected void LinkLoad()
        {
            Texture2D texture = Content.Load<Texture2D>("LinkDown");
            LinkDown = new AnimatedSprite(texture, 4, 4);

            texture = Content.Load<Texture2D>("LinkRight");
            LinkRight = new AnimatedSprite(texture, 4, 4);

            texture = Content.Load<Texture2D>("LinkUp");
            LinkUp = new AnimatedSprite(texture, 4, 4);

            texture = Content.Load<Texture2D>("LinkLeft");
            LinkLeft = new AnimatedSprite(texture, 4, 4);
        }
        protected override void LoadContent()
        {
            _spriteBatch = new SpriteBatch(GraphicsDevice);
            // TODO: use this.Content to load your game content here
            BackGround = Content.Load<Texture2D>("BackGround");
            LinkLoad();

        }

        protected void LinkUpdate(GameTime gameTime)
        {
            var kstate = Keyboard.GetState();

            if (kstate.IsKeyDown(Keys.Up)|| kstate.IsKeyDown(Keys.W))
            {
                spritePos.Y -= spriteSpeed * (float)gameTime.ElapsedGameTime.TotalSeconds;
                direction = Direction.Up;

            }
            if (kstate.IsKeyDown(Keys.Down) || kstate.IsKeyDown(Keys.S))
            {
                spritePos.Y += spriteSpeed * (float)gameTime.ElapsedGameTime.TotalSeconds;
                direction = Direction.Down;
            }

            if (kstate.IsKeyDown(Keys.Left) || kstate.IsKeyDown(Keys.A))
            {
                spritePos.X -= spriteSpeed * (float)gameTime.ElapsedGameTime.TotalSeconds;
                direction = Direction.Left;
            }
            if (kstate.IsKeyDown(Keys.Right) || kstate.IsKeyDown(Keys.D))
            {
                spritePos.X += spriteSpeed * (float)gameTime.ElapsedGameTime.TotalSeconds;
                direction = Direction.Right;
            }
            LinkDown.Update();
            LinkRight.Update();
            LinkUp.Update();
            LinkLeft.Update();
        }
        protected override void Update(GameTime gameTime)
        {
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed || Keyboard.GetState().IsKeyDown(Keys.Escape))
                Exit();

            // TODO: Add your update logic here
            LinkUpdate(gameTime);
           

            base.Update(gameTime);
        }

        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.CornflowerBlue);
            // TODO: Add your drawing code here
            _spriteBatch.Begin();
            _spriteBatch.Draw(BackGround, GraphicsDevice.Viewport.Bounds, Color.White);
            _spriteBatch.End();

            if(direction == Direction.Down )
            LinkDown.Draw(_spriteBatch, spritePos);
            else
            {
                if(direction == Direction.Right)LinkRight.Draw(_spriteBatch, spritePos);
                else
                {
                    if (direction == Direction.Up) LinkUp.Draw(_spriteBatch, spritePos);
                    else LinkLeft.Draw(_spriteBatch, spritePos);
                }
            }
            base.Draw(gameTime);
        }
    }
}
